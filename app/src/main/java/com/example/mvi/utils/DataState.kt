package com.example.mvi.utils


sealed class DataState<out T> {
    data class Success<out T>(val value: T) : DataState<T>()
    data class GenericError(val code: Int? = null, val error: String? = null) : DataState<Nothing>()
}