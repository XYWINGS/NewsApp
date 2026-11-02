package com.example.newsapp.presentation.screen.subscriptions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.entity.Article
import com.example.newsapp.domain.usecase.AddSubscriptionUseCase
import com.example.newsapp.domain.usecase.ClearAllArticlesUseCase
import com.example.newsapp.domain.usecase.GetAllSubscriptionsUseCase
import com.example.newsapp.domain.usecase.GetArticlesByTopicsUseCase
import com.example.newsapp.domain.usecase.RemoveSubscriptionUseCase
import com.example.newsapp.domain.usecase.UpdateArticlesForAllSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val removeSubscriptionsUseCase: RemoveSubscriptionUseCase,
    private val updateArticlesForAllSubscriptionsUseCase: UpdateArticlesForAllSubscriptionsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SubscriptionState.Display())
    val state = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionCommand) {
        when (command) {
//            Calling the suspend functions inside the viewmodel scope
            SubscriptionCommand.ClearArticles -> {
                viewModelScope.launch {
                    val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }

            SubscriptionCommand.ClickSubscribe -> {
                /*When the subscription happens we need to clear the query. So we use the state update*/
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = state.value.query.trim()
                        addSubscriptionUseCase(topic)
                        previousState.copy(query = "")
                    }
                }
            }

            is SubscriptionCommand.InputTopic -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        previousState.copy(query = command.query)
                    }
                }
            }

            SubscriptionCommand.RefreshData -> {
                viewModelScope.launch {
                    updateArticlesForAllSubscriptionsUseCase()
                }
            }

            is SubscriptionCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionsUseCase(command.topic)
                }
            }

            is SubscriptionCommand.ToggleTopicSelection -> {
                _state.update { previousState ->
                    val subscriptions = previousState.subscriptions.toMutableMap()
                    val isSelected = subscriptions[command.topic] ?: false
                    subscriptions[command.topic] = !isSelected
                    previousState.copy(subscriptions = subscriptions)
                }
            }
        }
    }

    /*We don't want to constaltly load data on every state change. We map it and call distinct
    * changed. Ww just react to the changfes in the selected topics*/
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedTopics() {
        _state.map { it.selectedTopics }.distinctUntilChanged().flatMapLatest {
                getArticlesByTopicsUseCase(topics = it)
            }.onEach {
                _state.update { previousState ->
                    previousState.copy(articles = it)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase().onEach { subscription ->
                _state.update { previousState ->
                    val updatedTopics = subscription.associateWith { topic ->
                        previousState.subscriptions[topic] ?: true
                    }
                    previousState.copy(subscriptions = updatedTopics)
                }
            }.launchIn(viewModelScope)
    }

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
