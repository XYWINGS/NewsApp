package com.example.newsapp.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsapp.domain.usecase.UpdateArticlesForAllSubscriptionsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val updateSubscribedArticlesUseCase: UpdateArticlesForAllSubscriptionsUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("RefreshDataWorker", "doWork: WORKER CALLED")
        updateSubscribedArticlesUseCase()
        Log.d("RefreshDataWorker", "doWork: WORKER FINISHED")
        return Result.success()
    }

}
