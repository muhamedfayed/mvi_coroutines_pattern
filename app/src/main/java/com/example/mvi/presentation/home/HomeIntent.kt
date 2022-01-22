package com.example.mvi.presentation.home

sealed class HomeIntent {
    object ShareVideo : HomeIntent()
    object GetData : HomeIntent()
}