package com.example.newsapp.domain.repository

import com.example.newsapp.domain.entity.Article
import kotlinx.coroutines.flow.Flow
/*Use Alt + Enter to generate the repo*/
interface NewsRepository {

    fun getAllSubscriptions(): Flow<List<String>>

    suspend fun addSubscription(topic: String)

    suspend fun updateArticlesForTopic(topic: String)

    suspend fun removeSubscription(topic: String)

    suspend fun updateArticlesForAllSubscriptions()

    fun getArticlesByTopics(topics: List<String>): Flow<List<Article>>

    suspend fun clearAllArticles(topics: List<String>)
}
