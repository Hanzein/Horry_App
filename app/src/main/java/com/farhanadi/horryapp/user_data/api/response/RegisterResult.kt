package com.farhanadi.horryapp.user_data.api.response

import com.google.gson.annotations.SerializedName

data class RegisterResult (
    @SerializedName("error") val error: Boolean?,
    @SerializedName("message") val message: String?
)
