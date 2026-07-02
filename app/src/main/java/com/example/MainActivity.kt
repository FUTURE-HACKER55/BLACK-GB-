package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    when (val screen = currentScreen) {
                        is Screen.Auth -> AuthScreen(viewModel)
                        is Screen.Main -> MainScreen(viewModel)
                        is Screen.ChatDetails -> ChatDetailsScreen(viewModel)
                        is Screen.NewStatus -> NewStatusScreen(viewModel)
                        is Screen.StatusViewer -> StatusViewerScreen(viewModel, screen.statusId)
                        is Screen.NewGroup -> NewGroupScreen(viewModel)
                    }
                }
            }
        }
    }
}
