package com.farhanadi.horryapp.data_testing

import com.farhanadi.horryapp.user_data.api.ApiService
import com.farhanadi.horryapp.user_data.api.response.LoginResponse
import com.farhanadi.horryapp.user_data.api.response.RegisterResult
import com.farhanadi.horryapp.user_data.api.response.StoryResponse
import com.farhanadi.horryapp.user_data.api.response.UploadResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {

    private val dummyRegisterResponse = DataRepo_Dummy.dummyRegisterResponse()
    private val dummyLoginResponse = DataRepo_Dummy.dummyLoginResponse()
    private val dummyStoryResponse = DataRepo_Dummy.dummyStoryWithLocationResponse()
    private val dummyAddNewStoryResponse = DataRepo_Dummy.dummyAddStoryResponse()

    override suspend fun login(email: String, password: String): LoginResponse =
        dummyLoginResponse

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResult =
        dummyRegisterResponse

    override suspend fun getStories(page: Int, size: Int, token: String): StoryResponse =
        dummyStoryResponse

    override suspend fun getStoriesWithLocation(loc: Int, token: String): StoryResponse =
        dummyStoryResponse

    override suspend fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?,
        token: String,
        type: String,
    ): UploadResult =
        dummyAddNewStoryResponse
}
