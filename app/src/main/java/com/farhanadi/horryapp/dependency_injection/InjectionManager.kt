package com.farhanadi.horryapp.dependency_injection

import android.content.Context
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.api.ApiClient
import com.farhanadi.horryapp.user_data.local_database.StoryDataStorage

object InjectionManager {
    fun provideRepository(context: Context): DataRepository {
        val database = StoryDataStorage.getDatabase(context)
        val apiService = ApiClient.getApiService()
        return DataRepository(database, apiService)
    }
}