package com.example.mvi.presentation.share

sealed class ShareViewState {

    object Init: ShareViewState()
    data class IsLoading(val isLoading: Boolean) : ShareViewState()
    data class DownloadedVideo(val localPath: String): ShareViewState()
    data class TrimmedVideo(val sharedPath: String): ShareViewState()
    object Error: ShareViewState()

}