
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

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.MediaItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Edge-to-edge initialization
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize components
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
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    private void verifyIntentExtras() {
        // Get and verify all intent extras
        postId = getIntent().getStringExtra("postId");
        postedByName = getIntent().getStringExtra("name");
        introUrl = getIntent().getStringExtra("introUrl");
        title = getIntent().getStringExtra("title");
        price = getIntent().getLongExtra("price", 0);
        duration = getIntent().getStringExtra("duration");
        rating = getIntent().getStringExtra("rate");
        description = getIntent().getStringExtra("desc");

        // Log verification data
        Log.d(TAG, "=== INTENT VERIFICATION ===");
        Log.d(TAG, "postId: " + (postId != null ? "✓" : "✗"));
        Log.d(TAG, "name: " + (postedByName != null ? "✓" : "✗"));
        Log.d(TAG, "introUrl: " + (introUrl != null ? "✓" : "✗"));
        Log.d(TAG, "title: " + (title != null ? "✓" : "✗"));
        Log.d(TAG, "price: " + price);
        Log.d(TAG, "duration: " + (duration != null ? "✓" : "✗"));
        Log.d(TAG, "rate: " + (rating != null ? "✓" : "✗"));
        Log.d(TAG, "desc: " + (description != null ? "✓" : "✗ (CRITICAL)"));
    }

    private void setupUI() {
        // Set basic course info
        binding.title.setText(title != null ? title : "No Title");
        binding.createdBy.setText(postedByName != null ? postedByName : "Unknown Creator");
        binding.rating.setText(rating != null ? rating : "0.0");
        binding.duration.setText(duration != null ? duration : "0h");
        binding.price.setText(String.valueOf(price));

        // Handle description with verification
        if (description != null && !description.trim().isEmpty()) {
            binding.description.setText(description);
            Log.d(TAG, "Description set successfully");
        } else {
            binding.description.setText("No description available");
            Log.w(TAG, "Empty or null description received");
            Toast.makeText(this, "Warning: Course description missing", Toast.LENGTH_SHORT).show();
        }

        // Setup click listeners
        binding.txtDescription.setOnClickListener(v -> toggleViews(true));
        binding.btnPlayList.setOnClickListener(v -> toggleViews(false));
    }

    private void initializePlayer() {
        try {
            if (introUrl == null || introUrl.isEmpty()) {
                throw new IllegalArgumentException("Intro video URL is empty");
            }

            player = new SimpleExoPlayer.Builder(this).build();
            binding.exoplayer2.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(introUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            Log.d(TAG, "Player initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Player initialization failed: " + e.getMessage());
            Toast.makeText(this, "Video playback error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvPlayList.setLayoutManager(layoutManager);

        adapter = new PlayListAdapter(this, playlist, (position, key, videoUrl, size) -> {
            playVideo(videoUrl);
        });
        binding.rvPlayList.setAdapter(adapter);
    }

    private void loadPlaylistData() {
        if (postId == null || postId.isEmpty()) {
            Log.e(TAG, "Invalid postId for playlist loading");
            loadingDialog.dismiss();
            return;
        }

        FirebaseDatabase.getInstance().getReference()
                .child("course")
                .child(postId)
                .child("playlist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        playlist.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                PlayListModel item = data.getValue(PlayListModel.class);
                                if (item != null) {
                                    item.setKey(data.getKey());
                                    playlist.add(item);
                                    Log.d(TAG, "Added playlist item: " + item.getKey());
                                }
                            }
                        } else {
                            Log.d(TAG, "No playlist items found");
                        }
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Playlist load error: " + error.getMessage());
                        loadingDialog.dismiss();
                        Toast.makeText(PlayListActivity.this, "Error loading playlist", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleViews(boolean showDescription) {
        binding.description.setVisibility(showDescription ? View.VISIBLE : View.GONE);
        binding.rvPlayList.setVisibility(showDescription ? View.GONE : View.VISIBLE);
    }

    private void playVideo(String videoUrl) {
        try {
            if (player == null) return;

            player.stop();
            player.clearMediaItems();
            player.setMediaItem(MediaItem.fromUri(videoUrl));
            player.prepare();
            player.play();
            Log.d(TAG, "Playing video: " + videoUrl);
        } catch (Exception e) {
            Log.e(TAG, "Video play error: " + e.getMessage());
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
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