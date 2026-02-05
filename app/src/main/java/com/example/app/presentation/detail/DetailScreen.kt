package com.example.app.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DetailScreen(modifier: Modifier = Modifier, id: String) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "详情页$id")
    }
}