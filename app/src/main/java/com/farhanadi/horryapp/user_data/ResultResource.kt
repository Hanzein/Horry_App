package com.farhanadi.horryapp.user_data

sealed class ResultResource<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultResource<T>()
    data class Error(val error: String) : ResultResource<Nothing>()
    object Loading : ResultResource<Nothing>()
}