package com.farhanadi.horryapp.user_data.api


import com.farhanadi.horryapp.user_data.api.response.LoginResponse
import com.farhanadi.horryapp.user_data.api.response.RegisterResult
import com.farhanadi.horryapp.user_data.api.response.StoryResponse
import com.farhanadi.horryapp.user_data.api.response.UploadResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResult

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") token: String
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun postImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?,
        @Header("Authorization") token: String,
        @Header("Accept") type: String
    ): UploadResult

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") loc: Int,
        @Header("Authorization") token: String
    ): StoryResponse

}
