package com.example.mvi.presentation.share

sealed class ShareIntent {
    data class DownloadVideo(val videoUrl: String) : ShareIntent()
    data class TrimVideo(val videoUrl: String, val minSeconds: Int, val maxSeconds: Int, val videoDuration: Int) : ShareIntent()

}