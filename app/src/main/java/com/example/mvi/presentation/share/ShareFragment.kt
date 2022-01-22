package com.example.mvi.presentation.share


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mvi.R
import com.example.mvi.databinding.FragmentShareBinding
import com.example.mvi.utils.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File


class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private var videoUrl = ""
    private val viewModel by viewModel<ShareViewModel>()
    private var isPlaying = false

    private var duration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoUrl = arguments?.getString(Constants.VIDEO_URL) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment'
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return _binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render()
        downloadVideo()

    }

    private fun downloadVideo() {
        lifecycleScope.launch {
            viewModel.intentChannel.send(ShareIntent.DownloadVideo(videoUrl))
        }
    }

    private fun render() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is ShareViewState.IsLoading -> {
                        if (it.isLoading) {
                            _binding!!.progressBarShare.visibility = View.VISIBLE
                            _binding!!.videoView.visibility = View.GONE
                            _binding!!.rangeSeekBar.visibility = View.GONE
                            _binding!!.rightText.visibility = View.GONE
                            _binding!!.leftText.visibility = View.GONE
                            _binding!!.shareWithFriendsBtn.visibility = View.GONE

                        } else {
                            _binding!!.progressBarShare.visibility = View.GONE
                            _binding!!.videoView.visibility = View.VISIBLE
                            _binding!!.rangeSeekBar.visibility = View.VISIBLE
                            _binding!!.rightText.visibility = View.VISIBLE
                            _binding!!.leftText.visibility = View.VISIBLE
                            _binding!!.shareWithFriendsBtn.visibility = View.VISIBLE
                        }
                    }
                    is ShareViewState.DownloadedVideo -> {
                        setupVideo(it.localPath)
                    }
                    is ShareViewState.TrimmedVideo -> {
                        shareTrimmedVideo(it.sharedPath)
                    }
                    is ShareViewState.Error -> {
                        Toast.makeText(
                            context, getString(R.string.something),
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigateUp()
                    }
                    ShareViewState.Init -> {
                        //init
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupVideo(localPath: String) {
        _binding!!.videoView.setVideoPath(localPath)
        _binding!!.videoView.start()

        _binding!!.videoView.setOnClickListener {
            isPlaying = if (isPlaying) {
                _binding!!.videoView.pause()
                false
            } else {
                _binding!!.videoView.start()
                true
            }
        }

        _binding!!.videoView.setOnPreparedListener {
            duration = it.duration / 1000
            _binding!!.leftText.text = "00:00:00"
            _binding!!.rightText.text = getTime(it.duration / 1000)

            _binding!!.rangeSeekBar.setRangeValues(0, duration)
            _binding!!.rangeSeekBar.selectedMaxValue = duration
            _binding!!.rangeSeekBar.selectedMinValue = 0
            _binding!!.rangeSeekBar.isEnabled = true

            _binding!!.rangeSeekBar.setOnRangeSeekBarChangeListener { _, minValue, maxValue ->
                _binding!!.videoView.seekTo(minValue as Int * 1000)
                _binding!!.leftText.text = getTime(minValue)
                _binding!!.rightText.text = getTime(maxValue as Int)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding!!.videoView.currentPosition >= _binding!!.rangeSeekBar.selectedMaxValue.toInt() * 1000)
                _binding!!.videoView.seekTo(_binding!!.rangeSeekBar.selectedMinValue.toInt() * 1000)

        }, 1000)


        _binding!!.shareWithFriendsBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.intentChannel.send(
                    ShareIntent.TrimVideo(
                        localPath,
                        _binding!!.rangeSeekBar.selectedMinValue.toInt() * 1000,
                        _binding!!.rangeSeekBar.selectedMaxValue.toInt() * 1000,
                        duration
                    )
                )
            }


        }


        _binding!!.rangeSeekBar.visibility = View.VISIBLE
        _binding!!.shareWithFriendsBtn.visibility = View.VISIBLE

    }

    private fun getTime(seconds: Int): String {
        val hr = seconds / 3600
        val rem = seconds % 3600
        val mn = rem / 60
        val sec = rem % 60

        return String.format("%02d", hr) + ":" + String.format(
            "%02d",
            mn
        ) + ":" + String.format("%02d", sec)
    }


    private fun shareTrimmedVideo(shareVideoPath: String) {
        val videoFile = File(shareVideoPath)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "video/*"
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        sharingIntent.putExtra(Intent.EXTRA_STREAM, videoFile.toUri())
        startActivity(Intent.createChooser(sharingIntent, "Share Video"))
        findNavController().navigateUp()
    }

}



