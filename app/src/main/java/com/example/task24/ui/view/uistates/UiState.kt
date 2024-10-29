package com.example.task24.ui.view.uistates

import com.example.task24.model.Contact

sealed class UiState {
    data object Loading:UiState()
    class Success (val data :List<Contact>, val message: String=""):UiState()
    data class Error(val message: String) : UiState()



}