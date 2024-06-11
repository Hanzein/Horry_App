package com.farhanadi.horryapp.user_ui_page.login

import androidx.lifecycle.ViewModel
import com.farhanadi.horryapp.user_data.DataRepository

class LoginViewModel(
    private val repository: DataRepository

) : ViewModel(){
    fun login(email: String, pass: String) = repository.login(email, pass)
}