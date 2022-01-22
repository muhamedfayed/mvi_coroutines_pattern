package com.example.mvi.presentation.home

sealed class HomeViewState {

    object Init: HomeViewState()
    data class IsLoading(val isLoading: Boolean) : HomeViewState()
    data class Data(val data: com.example.mvi.domain.posts.entity.Data): HomeViewState()
    data class Error(val error: String): HomeViewState()

}