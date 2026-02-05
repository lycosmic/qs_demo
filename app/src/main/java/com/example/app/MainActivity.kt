package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.navigation.MyNavDisplay
import com.example.app.navigation.Navigator
import com.example.app.navigation.NavigatorViewModelFactory
import com.example.app.ui.theme.QuantumStackDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val factory = remember { NavigatorViewModelFactory() }
            val navigator: Navigator = viewModel(factory = factory)

            QuantumStackDemoTheme {
                MyNavDisplay(navigator = navigator)
            }
        }
    }
}
