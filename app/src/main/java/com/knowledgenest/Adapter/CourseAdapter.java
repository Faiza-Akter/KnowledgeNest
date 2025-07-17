package com.knowledgenest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgenest.Model.CourseModel;
import com.knowledgenest.Model.UserModel;
import com.knowledgenest.PlayListActivity;
import com.knowledgenest.R;
import com.knowledgenest.databinding.RvCourseDesignBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    //Pushing CourseAdapter
    private static final String TAG = "CourseAdapter";
    private Context context;
    private ArrayList<CourseModel> courseList;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    public CourseAdapter(Context context, ArrayList<CourseModel> courseList) {
        this.context = context;
        this.courseList = courseList;
        this.database = FirebaseDatabase.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_course_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseModel course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateCourses(ArrayList<CourseModel> newCourses) {
        courseList.clear();
        courseList.addAll(newCourses);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvCourseDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvCourseDesignBinding.bind(itemView);
        }

        public void bind(CourseModel course) {
            // Load thumbnail with error handling
            loadThumbnail(course.getThumbnail());

            // Set course details
            binding.courseTitle.setText(course.getTitle());
            binding.coursePrice.setText(formatPrice(course.getPrice()));

            // Load creator info
            loadCreatorInfo(course.getPostedBy());

            // Set click listener
            itemView.setOnClickListener(v -> openCourseDetails(course));
        }

        private void loadThumbnail(String thumbnailUrl) {
            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                Picasso.get().load(thumbnailUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.courseImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Thumbnail loaded successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "Error loading thumbnail: " + e.getMessage());
                            }
                        });
            } else {
                binding.courseImage.setImageResource(R.drawable.placeholder);
                Log.w(TAG, "Empty thumbnail URL");
            }
        }

        private String formatPrice(long price) {
            return "$" + price;
        }

        private void loadCreatorInfo(String creatorId) {
            if (creatorId != null && !creatorId.isEmpty()) {
                database.getReference().child("admin_details").child(creatorId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserModel creator = snapshot.getValue(UserModel.class);
                                    if (creator != null) {
                                        binding.name.setText(creator.getName());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error loading creator info: " + error.getMessage());
                            }
                        });
            }
        }

        private void openCourseDetails(CourseModel course) {
            try {
                Intent intent = new Intent(context, PlayListActivity.class);
                intent.putExtra("postId", course.getPostId());
                intent.putExtra("title", course.getTitle());
                intent.putExtra("price", course.getPrice());
                intent.putExtra("duration", course.getDuration());
                intent.putExtra("desc", course.getDescription());
                intent.putExtra("introUrl", course.getIntrovideo());
                intent.putExtra("name", binding.name.getText().toString());
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening course: " + e.getMessage());
                Toast.makeText(context, "Error opening course", Toast.LENGTH_SHORT).show();
            }
        }
    }
}