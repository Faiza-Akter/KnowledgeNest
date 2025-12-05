
package com.knowledgenest.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgenest.Adapter.PlayListAdapter;
import com.knowledgenest.Model.PlayListModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivityPlayListBinding;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    private static final String TAG = "PlayListActivity";
    ActivityPlayListBinding binding;

    private String postId, postedByName, introUrl, title, duration, rating, description;
    private long price;

    private SimpleExoPlayer player;
    private ArrayList<PlayListModel> playlist = new ArrayList<>();
    private PlayListAdapter adapter;
    private Dialog loadingDialog;

    // ------------------ MEMENTO PATTERN VARIABLES ------------------
    private PlayerMemento savedState = null;   // object to store player state
    private String currentVideoUrl = "";       // last played video URL
    // ----------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Edge-to-edge rendering
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initLoadingDialog();
        verifyIntentExtras();
        setupUI();
        initializePlayer();
        setupRecyclerView();
        loadPlaylistData();
    }

    private void initLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void verifyIntentExtras() {
        postId = getIntent().getStringExtra("postId");
        postedByName = getIntent().getStringExtra("name");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getLongExtra("price", 0);
        duration = getIntent().getStringExtra("duration");
        rating = getIntent().getStringExtra("rate");
        description = getIntent().getStringExtra("desc");
    }

    private void setupUI() {
        binding.title.setText(title);
        binding.createdBy.setText(postedByName);
        binding.rating.setText(rating);
        binding.duration.setText(duration);
        binding.price.setText(String.valueOf(price));

        if (description != null && !description.isEmpty()) {
            binding.description.setText(description);
        } else {
            binding.description.setText("No description available");
        }

        binding.txtDescription.setOnClickListener(v -> toggleViews(true));
        binding.btnPlayList.setOnClickListener(v -> toggleViews(false));
    }

    private void initializePlayer() {
        try {
            player = new SimpleExoPlayer.Builder(this).build();
            binding.exoplayer2.setPlayer(player);
            currentVideoUrl = introUrl; // initial URL saved for Memento

            MediaItem mediaItem = MediaItem.fromUri(introUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } catch (Exception e) {
            Toast.makeText(this, "Video error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        binding.rvPlayList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayListAdapter(this, playlist, (position, key, videoUrl, size) -> {
            playVideo(videoUrl);  // switching videos updates Memento state automatically
        });
        binding.rvPlayList.setAdapter(adapter);
    }

    private void loadPlaylistData() {
        FirebaseDatabase.getInstance().getReference()
                .child("course")
                .child(postId)
                .child("playlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        playlist.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            PlayListModel item = data.getValue(PlayListModel.class);
                            if (item != null) {
                                item.setKey(data.getKey());
                                playlist.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                    }
                });
    }

    private void toggleViews(boolean showDescription) {
        binding.description.setVisibility(showDescription ? View.VISIBLE : View.GONE);
        binding.rvPlayList.setVisibility(showDescription ? View.GONE : View.VISIBLE);
    }

    // ----------------------- MEMENTO PATTERN ------------------------

    // Memento class to store video player state
    private static class PlayerMemento {
        String videoUrl;
        long position;

        PlayerMemento(String videoUrl, long position) {
            this.videoUrl = videoUrl;
            this.position = position;
        }
    }

    // Save video state before screen leaves
    private void savePlayerState() {
        if (player != null) {
            savedState = new PlayerMemento(currentVideoUrl, player.getCurrentPosition());
            Log.d(TAG, "Memento Saved → " + savedState.videoUrl + " @ " + savedState.position);
        }
    }

    // Restore state when screen returns
    private void restorePlayerState() {
        if (savedState != null && player != null) {
            player.setMediaItem(MediaItem.fromUri(savedState.videoUrl));
            player.prepare();
            player.seekTo(savedState.position);
            player.play();

            Log.d(TAG, "Memento Restored → " + savedState.videoUrl + " @ " + savedState.position);
        }
    }

    // When video is changed from playlist
    private void playVideo(String videoUrl) {
        try {
            currentVideoUrl = videoUrl; // Save URL for Memento

            player.stop();
            player.clearMediaItems();
            player.setMediaItem(MediaItem.fromUri(videoUrl));
            player.prepare();
            player.play();
        } catch (Exception e) {
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
        }
    }

    // Save & restore using Activity lifecycle
    @Override
    protected void onPause() {
        super.onPause();
        savePlayerState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restorePlayerState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player != null) {
            player.pause();
        }
    }
}
