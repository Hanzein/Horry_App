package com.farhanadi.horryapp.user_data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farhanadi.horryapp.user_data.api.ApiService
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_data.local_database.StoryDataStorage
import com.farhanadi.horryapp.user_data.local_database.RemotePaginationKeys
import com.farhanadi.horryapp.user_ui_page.utils.wrapEspressoIdlingResource

@OptIn(ExperimentalPagingApi::class)
class StoryPaginationMediator(
    private val Storydatabase: StoryDataStorage,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {


        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            return try {
                val responseData =
                    apiService.getStories(page, state.config.pageSize, token).listStory
                val endOfPaginationReached = responseData.isEmpty()

                Storydatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        Storydatabase.remoteKeysDao().deleteRemoteKeys()
                        Storydatabase.storyDao().deleteAll()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map {
                        RemotePaginationKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    Storydatabase.remoteKeysDao().insertAll(keys)
                    Storydatabase.storyDao().addStories(responseData)
                }
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } catch (exception: Exception) {
                Log.d("Remote Mediator", exception.message.toString())
                MediatorResult.Error(exception)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, ListStoryItem>
    ): RemotePaginationKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            Storydatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, ListStoryItem>
    ): RemotePaginationKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            Storydatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ListStoryItem>
    ): RemotePaginationKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                Storydatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
