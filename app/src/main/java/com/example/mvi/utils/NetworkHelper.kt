package com.example.mvi.utils


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException

abstract class NetworkHelper<in P, out R>{

    suspend fun  execute(parameter: P): Flow<DataState<R>> {
        return buildUseCase(parameter).buffer().catch { e ->
            when (e) {
                is HttpException -> {
                        val code = e.code()
                    val errorResponse = convertErrorBody(e)
                    emit(DataState.GenericError(code, errorResponse))
                }
                else -> {
                    emit(DataState.GenericError(null,  "Something went wrong, Please check your internet connection and retry"))
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): String? {
        return try {
            return when {
                throwable.code() == 404 -> {
                    "Not found"
                }
                throwable.code() == 500 -> {
                    "Server Error, Please try later"
                }
                else -> {
                    "Something went wrong"
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            "Something went wrong. Please check your internet and try again"
        }
    }

    abstract suspend fun buildUseCase(parameter: P): Flow<DataState<R>>
}