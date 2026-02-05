package com.example.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


object NavigatorViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Navigator::class.java)) {
            return Navigator() as T
        }
        throw IllegalArgumentException("未知的ViewModel类")
    }
}