package com.farhanadi.horryapp.user_ui_page.signup

import androidx.lifecycle.ViewModel
import com.farhanadi.horryapp.user_data.DataRepository

class SignUpViewModel(
    private val repository: DataRepository
) : ViewModel(){
    fun register(name: String, email: String, pass: String) = repository.register(name, email, pass)
}