package com.example.mvi.presentation.share

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.io.File

class ShareViewModel : ViewModel() {

    val intentChannel = Channel<ShareIntent>(Channel.UNLIMITED)
    private val _viewState = MutableStateFlow<ShareViewState>(ShareViewState.Init)
    val state: StateFlow<ShareViewState> get() = _viewState

    init {
        processIntent()
    }

    //process
    private fun processIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                when (it) {
                    is ShareIntent.DownloadVideo -> downloadVideo(it.videoUrl)
                    is ShareIntent.TrimVideo -> trimAndShare(it.videoUrl,it.minSeconds,it.maxSeconds,it.videoDuration)
//                    is ShareIntent.ShareOutSide -> shareOutSide(it.videoUrl,it.context)
                }
            }
        }
    }

    private fun downloadVideo(videoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        val fileName = "video${videoUrl.substring(videoUrl.length - 10)}"
        PRDownloader.download(videoUrl,
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), fileName)
            .build()
            .setOnStartOrResumeListener {
                Log.i("Download", "start")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    // Trim
                    Log.i("Download", "Done")
                    _viewState.value = ShareViewState.DownloadedVideo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+fileName)
                    setLoading(false)
                }
                override fun onError(error: com.downloader.Error?) {
                    // Do Nothing
                    Log.i("Download", "Error")
                    onError()
                }
            })
        }
    }

    private fun setLoading(flag: Boolean) {
        viewModelScope.launch {
            _viewState.value = ShareViewState.IsLoading(flag)
        }
    }

    private fun onError() {
        viewModelScope.launch {
            _viewState.value = ShareViewState.Error
        }
    }

    private fun trimAndShare(videoUrl: String,minSeconds: Int, maxSeconds: Int,videoDuration:Int) {

        viewModelScope.launch(Dispatchers.IO) {
            setLoading( true)
        if (minSeconds == 0 && maxSeconds == videoDuration*1000){
            trimmed(videoUrl)
        }else{
            val output =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
            if (!output.exists()) {
                output.mkdir()
            }
            val saveLocation = File(output, "trim" + (0..999999).random().toString() + ".mp4")
            val command = arrayOf(
                "-ss",
                "" + minSeconds / 1000,
                "-y",
                "-i",
                videoUrl,
                "-t",
                "" + (maxSeconds - minSeconds) / 1000,
                "-vcodec",
                "mpeg4",
                "-threads",
                "5",
                "-preset",
                "ultrafast",
                saveLocation.absolutePath
            )

            when (val rc = FFmpeg.execute(command)) {
                Config.RETURN_CODE_SUCCESS -> {
                    Log.i("FFmp", "Command execution completed successfully.")
                    trimmed(saveLocation.absolutePath)
                }
                Config.RETURN_CODE_CANCEL -> {
                    Log.i("FFmp", "Command execution cancelled by user.")
                  onError()
                }
                else -> {
                    Log.i(
                        "FFmp",
                        String.format("Command execution failed with rc=%d and the output below.", rc)
                    )
                   onError()
                }
            }
        }
        }
    }

    private fun trimmed(mySharedVideoPath:String) {
        viewModelScope.launch {
            _viewState.value = ShareViewState.TrimmedVideo(mySharedVideoPath)
        }
    }

}