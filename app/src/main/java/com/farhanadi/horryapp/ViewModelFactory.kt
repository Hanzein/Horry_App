package com.farhanadi.horryapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.dependency_injection.InjectionManager
import com.farhanadi.horryapp.user_ui_page.add.AddViewModel
import com.farhanadi.horryapp.user_ui_page.ui.home.HomeViewModel
import com.farhanadi.horryapp.user_ui_page.login.LoginViewModel
import com.farhanadi.horryapp.user_ui_page.signup.SignUpViewModel
import com.farhanadi.horryapp.user_ui_page.ui.map.MapViewModel

class ViewModelFactory(
    private val repository: DataRepository
) : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel (repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel (repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(InjectionManager.provideRepository(context))
            }.also {
                instance = it
            }
    }

}