package com.example.murmurcodepath;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.murmurcodepath.databinding.MrAudioViewBinding;
import com.example.murmurcodepath.databinding.MrMainActivityBinding;
import com.example.murmurcodepath.databinding.MrTagBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.Set;

public class MrMainActivity extends AppCompatActivity {

    @Nullable private MrMainActivityBinding binding;
    @Nullable private AnimatedVectorDrawableCompat addToCollection;
    @Nullable private AnimatedVectorDrawableCompat removeFromCollection;

    @Nullable private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private boolean showingAddToCollection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MurmurCodePath);
        binding = MrMainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addToCollection = AnimatedVectorDrawableCompat.create(
            this,
            R.drawable.mr_avd_add_to_collection
        );

        removeFromCollection = AnimatedVectorDrawableCompat.create(
            this,
            R.drawable.mr_avd_remove_from_collection
        );

        binding.getRoot().postDelayed(new Runnable() {
            @Override public void run() {
                updateViewsFromAudio(Audio.getTestAudio());
            }
        }, 500);
    }

    private void updateViewsFromAudio(Audio audio) {
        ViewExtensions.fadeOut(binding.mrProgressSpinner);
        ViewExtensions.fadeIn(binding.mrAudioViewStub);

        MrAudioViewBinding audioViewBinding =
            MrAudioViewBinding.bind(findViewById(R.id.mr_audio_view));

        MediaItem mediaItem = MediaItem.fromUri(audio.getPlayableAudioLink());
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare();

        PlayerView playerView = audioViewBinding.mrPlayer;
        playerView.setPlayer(player);
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerShowTimeoutMs(0);

        audioViewBinding.mrTitle.setText(audio.getTitle());
        audioViewBinding.mrAudioAuthor.setText(audio.getArtistName());
        audioViewBinding.mrDescription.setText(audio.getDescription());
        audioViewBinding.mrImageAuthor.setText("Jenny Mills via Unsplash");

        ImageButton addRemoveCollectionButton =
            audioViewBinding.mrPlayer.findViewById(R.id.mr_add_remove_collection);

        if (addRemoveCollectionButton != null) {
            addRemoveCollectionButton.setImageDrawable(addToCollection);
            addRemoveCollectionButton.setOnClickListener(v ->
            {
                AnimatedVectorDrawableCompat drawable = (showingAddToCollection) ? addToCollection :
                    removeFromCollection;

                addRemoveCollectionButton.setImageDrawable(drawable);
                drawable.start();
                showingAddToCollection = !showingAddToCollection;
            });
        }

        ImageButton searchCollection =
            audioViewBinding.mrPlayer.findViewById(R.id.mr_search_collection);

        if (searchCollection != null) {
            searchCollection.setOnClickListener(v ->
            {
//            val intent = Intent(this @MrMainActivity,MrCollectionActivity:: class.java)
//            startActivity(intent)
            });
        }

        setUpTags(
            audioViewBinding.getRoot(),
            audioViewBinding.mrTags,
            audio.getTags()
        );

        playerView.showController();
    }

    private void setUpTags(ViewGroup rootView, Flow tagsView, Set<String> tags) {
        for (String tag : tags) {
            View view = LayoutInflater.from(this).inflate(R.layout.mr_tag, binding.getRoot(), false);

            view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setId(View.generateViewId());

            MrTagBinding tagBinding = MrTagBinding.bind(view);
            tagBinding.mrTag.setText(tag);

            rootView.addView(view);
            tagsView.addView(view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
    }
}