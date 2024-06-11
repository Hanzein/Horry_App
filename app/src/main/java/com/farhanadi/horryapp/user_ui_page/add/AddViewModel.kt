package com.farhanadi.horryapp.user_ui_page.add

import androidx.lifecycle.ViewModel
import com.farhanadi.horryapp.user_data.DataRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(
    private val repository: DataRepository
) : ViewModel() {

    fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        token: String,
        multiPort: String
    ) = repository.postImage(file, description, lat, lon, token, multiPort)
}
