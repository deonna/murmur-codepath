package com.example.murmurcodepath

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.murmurcodepath.Audio.Companion.getTestAudio
import com.example.murmurcodepath.ViewExtensions.fadeIn
import com.example.murmurcodepath.ViewExtensions.fadeOut
import com.example.murmurcodepath.databinding.MrAudioViewBinding
import com.example.murmurcodepath.databinding.MrMainActivityBinding
import com.example.murmurcodepath.databinding.MrTagBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util

class MrMainActivity : AppCompatActivity() {
    private var binding: MrMainActivityBinding? = null
    private var addToCollection: AnimatedVectorDrawableCompat? = null
    private var removeFromCollection: AnimatedVectorDrawableCompat? = null
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var showingAddToCollection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MurmurCodePath)
        binding = MrMainActivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        addToCollection = AnimatedVectorDrawableCompat.create(
            this,
            R.drawable.mr_avd_add_to_collection
        )
        removeFromCollection = AnimatedVectorDrawableCompat.create(
            this,
            R.drawable.mr_avd_remove_from_collection
        )
        binding!!.root.postDelayed({ updateViewsFromAudio(getTestAudio()) }, 500)
    }

    private fun updateViewsFromAudio(audio: Audio) {
        fadeOut(binding!!.mrProgressSpinner)
        fadeIn(binding!!.mrAudioViewStub)

        val audioViewBinding = MrAudioViewBinding.bind(findViewById(R.id.mr_audio_view))
        val mediaItem = MediaItem.fromUri(Uri.parse(audio.playableAudioLink))

        player!!.setMediaItem(mediaItem)
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare()

        val playerView = audioViewBinding.mrPlayer
        playerView.player = player
        playerView.controllerHideOnTouch = false
        playerView.controllerShowTimeoutMs = 0

        audioViewBinding.mrTitle.text = audio.title
        audioViewBinding.mrAudioAuthor.text = audio.artistName
        audioViewBinding.mrDescription.text = audio.description
        audioViewBinding.mrImageAuthor.text = "Jenny Mills via Unsplash"

        val addRemoveCollectionButton =
            audioViewBinding.mrPlayer.findViewById<ImageButton>(R.id.mr_add_remove_collection)
        if (addRemoveCollectionButton != null) {
            addRemoveCollectionButton.setImageDrawable(addToCollection)
            addRemoveCollectionButton.setOnClickListener { v: View? ->
                val drawable = if (showingAddToCollection) addToCollection else removeFromCollection
                addRemoveCollectionButton.setImageDrawable(drawable)
                drawable!!.start()
                showingAddToCollection = !showingAddToCollection
            }
        }
        val searchCollection =
            audioViewBinding.mrPlayer.findViewById<ImageButton>(R.id.mr_search_collection)
        searchCollection?.setOnClickListener { v: View? -> }
        setUpTags(
            audioViewBinding.root,
            audioViewBinding.mrTags,
            audio.tags
        )
        playerView.showController()
    }

    private fun setUpTags(rootView: ViewGroup, tagsView: Flow, tags: Set<String>) {
        for (tag in tags) {
            val view = LayoutInflater.from(this).inflate(R.layout.mr_tag, binding!!.root, false)
            view.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            view.id = View.generateViewId()
            val tagBinding = MrTagBinding.bind(view)
            tagBinding.mrTag.text = tag
            rootView.addView(view)
            tagsView.addView(view)
        }
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
    }
}