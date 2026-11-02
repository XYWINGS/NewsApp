package com.example.newsapp.presentation.screen.subscriptions.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.newsapp.R
import com.example.newsapp.presentation.screen.subscriptions.viewmodel.SubscriptionsViewModel

@Composable
fun SubscriptionScreen(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionTopBar(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onClearArticleClick: () -> Unit
) {
    TopAppBar(modifier = modifier, title = {
        Text(text = stringResource(R.string.subscription_title))
    }, actions = {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onRefreshClick()
                }
                .padding(8.dp),
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.update_articles)
        )
    })
}
