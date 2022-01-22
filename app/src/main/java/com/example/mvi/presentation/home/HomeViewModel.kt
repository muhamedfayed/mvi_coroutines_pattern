package com.example.mvi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi.domain.posts.usercase.PostsUseCase
import com.example.mvi.utils.DataState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val postsUseCase: PostsUseCase) : ViewModel() {

    val  intentChannel= Channel<HomeIntent>(Channel.UNLIMITED)
    private val _viewState = MutableStateFlow<HomeViewState>(HomeViewState.Init)

    val state: StateFlow<HomeViewState> get() = _viewState

    init {
        processIntent()
    }

    //process
    private fun processIntent(){
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                when(it){
                    is HomeIntent.ShareVideo -> shareVideo()
                    is HomeIntent.GetData -> getData()
                }
            }
        }
    }

    private fun shareVideo() {
        TODO("Not yet implemented")
    }

    //reduce
    private fun getData()= viewModelScope.launch {
        setLoading(true)
        postsUseCase.execute(PostsUseCase.Params()).collect {
            when (it) {
                is DataState.GenericError -> {
                    it.error?.let { err ->
                        _viewState.value = HomeViewState.Error(err)
                    }
                }
                is DataState.Success -> {
                    it.value.data?.let { result ->
                        _viewState.value = HomeViewState.Data(result)
                    }
                }
            }
            setLoading(false)
        }
    }

    private fun setLoading(flag:Boolean) {
        viewModelScope.launch {
            _viewState.value = HomeViewState.IsLoading(flag)
        }
    }

}