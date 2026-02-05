package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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
            val navigator: Navigator = viewModel(factory = NavigatorViewModelFactory)

            QuantumStackDemoTheme {
                MyNavDisplay(navigator = navigator, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
