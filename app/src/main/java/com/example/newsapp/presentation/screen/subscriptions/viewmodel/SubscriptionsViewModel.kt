package com.example.newsapp.presentation.screen.subscriptions.viewmodel

import androidx.lifecycle.ViewModel
import com.example.newsapp.domain.entity.Article
import com.example.newsapp.domain.usecase.AddSubscriptionUseCase
import com.example.newsapp.domain.usecase.ClearAllArticlesUseCase
import com.example.newsapp.domain.usecase.GetAllSubscriptionsUseCase
import com.example.newsapp.domain.usecase.GetArticlesByTopicsUseCase
import com.example.newsapp.domain.usecase.RemoveSubscriptionUseCase
import com.example.newsapp.domain.usecase.UpdateArticlesForAllSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val removeSubscriptionsUseCase: RemoveSubscriptionUseCase,
    private val updateSubscriptionUseCase: UpdateArticlesForAllSubscriptionsUseCase
) : ViewModel() {

}


/*Actions user can invoke: There will be many button on the screen, but if they doesn't change the
* screen state and focused on the navigation, they belong to the navigation layer
*/
sealed interface SubscriptionCommand {
    data class InputTopic(val query: String) : SubscriptionCommand
    data object ClickSubscribe : SubscriptionCommand
    data object RefreshData : SubscriptionCommand
    data class ToggleTopicSelection(val topic: String) : SubscriptionCommand
    data object ClearArticles : SubscriptionCommand
    data class RemoveSubscription(val topic: String) : SubscriptionCommand
}

sealed interface SubscriptionState {
    data object Loading : SubscriptionState
    data class Display(
        val query: String = "",
        val subscriptions: Map<String, Boolean> = mapOf(),
        val articles: List<Article> = listOf()
    ) : SubscriptionState {
        val selectedTopics: List<String>
            get() = subscriptions.filter { it.value }.map { it.key }

        val subscribeButtonEnabled: Boolean
            get() = query.isNotBlank()
    }
}
