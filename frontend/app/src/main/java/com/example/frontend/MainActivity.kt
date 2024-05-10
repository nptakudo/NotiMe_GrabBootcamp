package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.ui.screens.subscription.SubscriptionRoute
import com.example.frontend.ui.theme.FrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    HomeRoute(
//                        viewModel = hiltViewModel(),
//                        onArticleClick = {},
//                        onNavigateNavBar = {},
//                        onAboutClick = {},
//                        onLogOutClick = {}
//                    )
//                    ReaderRoute(
//                        viewModel = hiltViewModel(),
//                        onReadAnotherArticle = {},
//                        onBack = {}
//                    )
                    SubscriptionRoute(
                        viewModel = hiltViewModel(),
                        onNavigateNavBar = {},
                    )
                }
            }
        }
    }
}