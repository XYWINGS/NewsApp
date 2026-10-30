package com.example.newsapp.data.remote.service

import retrofit2.http.Query
import com.example.newsapp.data.remote.dto.NewsResponseDto
import retrofit2.http.GET

interface NewsApiService {

    @GET("v2/everything?sortBy=publishedAt&apiKey=dccaaadf317043ad8c8c38a7d50aa58d")
    suspend fun loadArticles(
        @Query("q") topic: String,
//        @Query("language") language: String
    ): NewsResponseDto
}
