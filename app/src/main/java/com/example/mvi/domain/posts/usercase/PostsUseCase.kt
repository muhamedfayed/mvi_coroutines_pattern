package com.example.mvi.domain.posts.usercase

import com.example.mvi.domain.posts.PostsRepository
import com.example.mvi.domain.posts.entity.PostsResponse
import com.example.mvi.utils.DataState
import com.example.mvi.utils.NetworkHelper
import kotlinx.coroutines.flow.Flow

class PostsUseCase(private val postsRepository: PostsRepository):
    NetworkHelper<PostsUseCase.Params, PostsResponse>() {

    class Params
    override suspend fun buildUseCase(parameter: Params): Flow<DataState<PostsResponse>> {
        return postsRepository.getPostsList()
    }

}