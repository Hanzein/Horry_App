package com.farhanadi.horryapp.user_data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.farhanadi.horryapp.user_data.api.ApiService
import com.farhanadi.horryapp.user_data.api.response.UploadResult
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_data.api.response.LoginResponse
import com.farhanadi.horryapp.user_data.api.response.RegisterResult
import com.farhanadi.horryapp.user_data.api.response.StoryResponse
import com.farhanadi.horryapp.user_data.local_database.StoryDataStorage
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DataRepository(
    private val storyDatabase: StoryDataStorage,
    private val apiService: ApiService
) {
    fun register(
        name: String,
        email: String,
        pass: String
    ): LiveData<ResultResource<RegisterResult>> = liveData {
        emit(ResultResource.Loading)
        try {
            val response = apiService.register(name, email, pass)
            emit(ResultResource.Success(response))
        } catch (e: Exception) {
            Log.d("register", e.message.toString())
            emit(ResultResource.Error(e.message.toString()))
        }
    }

    fun login(email: String, pass: String): LiveData<ResultResource<LoginResponse>> = liveData {
        emit(ResultResource.Loading)
        try {
            val response = apiService.login(email, pass)
            emit(ResultResource.Success(response))
        } catch (e: Exception) {
            Log.d("login", e.message.toString())
            emit(ResultResource.Error(e.message.toString()))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryPaginationMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        token: String,
        multiPort: String,
    ): LiveData<ResultResource<UploadResult>> = liveData {
        emit(ResultResource.Loading)
        try {
            val response = apiService.postImage(file, description, lat, lon, token, multiPort)
            emit(ResultResource.Success(response))
        } catch (e: Exception) {
            Log.d("post_image", e.message.toString())
            emit(ResultResource.Error(e.message.toString()))
        }
    }

    fun getWithLocation(location: Int, token: String): LiveData<ResultResource<StoryResponse>> =
        liveData {
            emit(ResultResource.Loading)
            try {
                val response = apiService.getStoriesWithLocation(location, token)
                emit(ResultResource.Success(response))
            } catch (e: Exception) {
                Log.d("story_maps", e.message.toString())
                emit(ResultResource.Error(e.message.toString()))
            }
        }
}