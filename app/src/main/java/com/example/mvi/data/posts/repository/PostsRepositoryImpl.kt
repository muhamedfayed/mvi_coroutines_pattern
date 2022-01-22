package com.example.mvi.data.posts.repository

import com.example.mvi.data.posts.api.PostsApi
import com.example.mvi.domain.posts.PostsRepository
import com.example.mvi.domain.posts.entity.PostsResponse
import com.example.mvi.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostsRepositoryImpl(private val postsApi: PostsApi):PostsRepository {
    override suspend fun getPostsList(): Flow<DataState<PostsResponse>> = flow {
        emit(DataState.Success(postsApi.getPosts()))
    }
}