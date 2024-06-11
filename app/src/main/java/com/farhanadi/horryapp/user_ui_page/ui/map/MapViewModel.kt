package com.farhanadi.horryapp.user_ui_page.ui.map

import androidx.lifecycle.ViewModel
import com.farhanadi.horryapp.user_data.DataRepository

class MapViewModel (
    private val repository: DataRepository
) : ViewModel(){
    fun getWithLocation(location: Int, token: String) =
        repository.getWithLocation(location, token)
}