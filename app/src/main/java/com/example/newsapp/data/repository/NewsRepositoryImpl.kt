package com.example.newsapp.data.repository

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.newsapp.data.background.RefreshDataWorker
import com.example.newsapp.data.dao.NewsDao
import com.example.newsapp.data.local.ArticleDbModel
import com.example.newsapp.data.local.SubscriptionDbModel
import com.example.newsapp.data.mapper.toDbModel
import com.example.newsapp.data.mapper.toEntities
import com.example.newsapp.data.remote.service.NewsApiService
import com.example.newsapp.domain.entity.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
    private val workManager: WorkManager
) : NewsRepository {

    init {
        startBackgroundRefresh()
    }

    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { it.topic }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic = topic))
    }

    override suspend fun updateArticlesForTopic(topic: String) {
        val articles = loadArticles(topic = topic)
        newsDao.addArticles(articles)
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticles(topic = topic).toDbModel(topic = topic)
        } catch (e: Exception) {
            /*If we cancel a coroutine, a cancellation exception is thrown, it should not be
            * handled inside the try/catch block. Handle it separately*/
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepositoryImpl", e.stackTraceToString())
            listOf()
        }
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic = topic))
    }

    override suspend fun updateArticlesForAllSubscriptions() {
        /* Here we load articles for each topic one by one, instead of that using coroutines scope
         * with launch let us load them independent of each other parallaly */
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    updateArticlesForTopic(it.topic)
                }
            }
        }
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics = topics).map {
            it.toEntities()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics = topics)
    }

    private fun startBackgroundRefresh() {
        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            repeatInterval = 15L, repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "RefreshData",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }
}
