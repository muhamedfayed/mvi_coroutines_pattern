package com.example.mvi.data.posts.api

import com.example.mvi.domain.posts.entity.PostsResponse
import retrofit2.http.GET

interface PostsApi {
    @GET("list")
    suspend fun getPosts(): PostsResponse
}