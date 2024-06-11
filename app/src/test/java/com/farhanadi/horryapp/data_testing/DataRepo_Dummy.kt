package com.farhanadi.horryapp.data_testing

import com.farhanadi.horryapp.user_data.api.response.LoginResponse
import com.farhanadi.horryapp.user_data.api.response.LoginResult
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_data.api.response.RegisterResult
import com.farhanadi.horryapp.user_data.api.response.StoryResponse
import com.farhanadi.horryapp.user_data.api.response.UploadResult

object DataRepo_Dummy {

    fun dummyLoginResponse(): LoginResponse =
        LoginResponse(
            LoginResult("name", "id", "token"),
            false,
            "token"
        )

    fun dummyRegisterResponse(): RegisterResult =
        RegisterResult(false, "success")

    fun generateDummyStoryItem(i: Int): ListStoryItem =
        ListStoryItem(
            i.toString(),
            "created + $i",
            "name + $i",
            "description + $i",
            i.toDouble(),
            "id + $i",
            i.toDouble()
        )

    fun dummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = mutableListOf()
        for (i in 0..100) {
            items.add(generateDummyStoryItem(i))
        }
        return items
    }

    fun dummyStoryWithLocationResponse(): StoryResponse {
        val items: MutableList<ListStoryItem> = mutableListOf()
        for (i in 0..100) {
            items.add(generateDummyStoryItem(i))
        }
        return StoryResponse(items, false, "success")
    }

    fun dummyAddStoryResponse(): UploadResult =
        UploadResult(false, "success")
}
