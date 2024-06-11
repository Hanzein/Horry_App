package com.farhanadi.horryapp.user_ui_page.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem

class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> =
        repository.getStories(token).cachedIn(viewModelScope)
}