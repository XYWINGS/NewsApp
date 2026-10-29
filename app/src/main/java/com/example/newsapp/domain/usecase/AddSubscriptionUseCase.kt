package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(topic: String) {
        /*The business logics is handled by the domain layer. If it needs to get data after saving it
        *tells the data layer to do so*/
        newsRepository.addSubscription(topic)
        newsRepository.updateArticlesForTopic(topic)
    }
}