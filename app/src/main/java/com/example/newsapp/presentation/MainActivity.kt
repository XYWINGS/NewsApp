package com.example.newsapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.presentation.screen.subscriptions.composable.SubscriptionScreen
import com.example.newsapp.presentation.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repository.addSubscription("Kotlin")
            repository.updateArticlesForTopic("Kotlin")
        }
        setContent {
            NewsAppTheme {
                SubscriptionScreen(
                    onNavigateToSettings = {},
                )
            }
        }
    }
}
