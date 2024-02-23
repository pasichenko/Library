package com.makspasich.library.screens.edit.tag_dialog

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makspasich.library.model.service.StorageService
import kotlinx.coroutines.launch

class CreateTagViewModel : ViewModel() {
    private val storageService = StorageService()
    val tagName = mutableStateOf("")

    fun onTagNameChanged(newValue: String) {
        tagName.value = newValue
    }

    fun onDoneClick(popUpScreen: () -> Unit) {
        viewModelScope.launch {
            storageService.saveIfNotExists(tagName.value)
            popUpScreen()
        }
    }
}