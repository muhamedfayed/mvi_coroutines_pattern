package com.example.mvi.presentation.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mvi.R
import com.example.mvi.databinding.FragmentPostViewBinding
import com.example.mvi.domain.posts.entity.PostEntity
import com.example.mvi.utils.Constants
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PostViewFragment : Fragment() {

    private var _binding: FragmentPostViewBinding? = null
    private var player: SimpleExoPlayer? = null
    private var post: PostEntity? = null
    private var storyUrl: String? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var played = true

    companion object {
        fun newInstance(videoDataModel: PostEntity) = PostViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.DATA_STORE, videoDataModel)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment'
        _binding = FragmentPostViewBinding.inflate(inflater, container, false)

        return _binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        post = arguments?.getParcelable(Constants.DATA_STORE)
        prepareVideoPlayer()
        setData()

        _binding!!.videoController.setOnClickListener {
            player?.playWhenReady = !played
            played = !played
        }

        _binding!!.shareImage.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(
                Constants.VIDEO_URL,
                post?.media_base_url + post?.recording_details?.recording_url
            )
            findNavController().navigate(
                R.id.action_homeFragment_to_shareFragment,
                bundle
            )
        }
    }

    private fun prepareVideoPlayer() {
        val adaptiveTrackSelection: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())

        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(requireContext()),
            DefaultTrackSelector(adaptiveTrackSelection),
            DefaultLoadControl()
        )
        _binding!!.playerView.useController = false
        _binding!!.playerView.player = player

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        dataSourceFactory = DefaultDataSourceFactory(
            requireContext(),
            Util.getUserAgent(requireContext(), "Mvi"), defaultBandwidthMeter
        )


    }

    private fun setData() {
        Glide.with(requireContext())
            .load(post?.media_base_url + post?.recording_details?.cover_img_url)
            .into(_binding!!.coverImage)
        storyUrl = post?.recording_details?.streaming_hls
        storyUrl?.let { prepareExoPlayer(it) }


    }

    private fun prepareExoPlayer(linkUrl: String) {
        val mediaUri: Uri = Uri.parse(linkUrl)
        val mediaSource: MediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(
            mediaUri
        )

        val loopingSource = LoopingMediaSource(mediaSource)
        player?.prepare(loopingSource)
        player?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    //media is ready
                    _binding!!.coverImage.visibility = View.GONE
                }
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                // leave empty
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                // leave empty
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                // leave empty

            }

            override fun onLoadingChanged(isLoading: Boolean) {
                // leave empty
            }

            override fun onPositionDiscontinuity(reason: Int) {
                // leave empty
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                // leave empty
            }

            override fun onSeekProcessed() {
                // leave empty
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                // leave empty
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                // leave empty
            }
        })

    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        player?.stop(true)
        player?.release()
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    private fun pauseVideo() {
        player?.playWhenReady = false
        player?.seekTo(0)
    }

    override fun onResume() {
        videoRestart()
        super.onResume()
    }

    private fun videoRestart() {
        if (player == null) {
            storyUrl?.let { prepareExoPlayer(it) }
        } else {
            player?.seekTo(0)
            player?.playWhenReady = true
        }
    }

}