package com.example.mvi.domain.posts

import com.example.mvi.domain.posts.entity.PostsResponse
import com.example.mvi.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    suspend fun getPostsList(): Flow<DataState<PostsResponse>>
}