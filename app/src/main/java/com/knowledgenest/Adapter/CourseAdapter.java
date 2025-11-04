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
import com.knowledgenest.Activity.PlayListActivity;
import com.knowledgenest.R;
import com.knowledgenest.databinding.RvCourseDesignBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    //Pushing CourseAdapter
    private static final String TAG = "CourseAdapter";
    private final Context context;
    private final ArrayList<CourseModel> courseList;
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

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
        // ✅ Use the built-in Factory to transform raw model into a CourseItem
        CourseModel raw = courseList.get(position);
        CourseItem courseItem = ContentFactory.create("course", raw);
        holder.bind(courseItem);
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

        public void bind(CourseItem course) {
            // Load thumbnail with error handling
            loadThumbnail(course.getThumbnail());

            // Set course details
            binding.courseTitle.setText(nullSafe(course.getTitle()));
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
                                        binding.name.setText(nullSafe(creator.getName()));
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

        private void openCourseDetails(CourseItem course) {
            try {
                Intent intent = new Intent(context, PlayListActivity.class);
                intent.putExtra("postId", nullSafe(course.getPostId()));
                intent.putExtra("title", nullSafe(course.getTitle()));
                intent.putExtra("price", course.getPrice());
                intent.putExtra("duration", nullSafe(course.getDuration()));
                intent.putExtra("desc", nullSafe(course.getDescription()));
                intent.putExtra("introUrl", nullSafe(course.getIntrovideo()));
                intent.putExtra("name", binding.name.getText() != null ? binding.name.getText().toString() : "");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening course: " + e.getMessage());
                Toast.makeText(context, "Error opening course", Toast.LENGTH_SHORT).show();
            }
        }

        private String nullSafe(String s) {
            return s == null ? "" : s;
        }
    }

    // ---------------------------------------------------------------------
    // ✅ Built-in Factory + DTO (no new package/file needed)
    // ---------------------------------------------------------------------

    /**
     * Simple factory to create different content items. For now it returns CourseItem,
     * but it's ready to branch out (e.g., "videoCourse", "pdfCourse", "quizCourse").
     */
    private static class ContentFactory {
        static CourseItem create(String type, CourseModel source) {
            if (type == null) throw new IllegalArgumentException("type == null");
            switch (type.toLowerCase()) {
                case "course":
                    return CourseItem.from(source);
                // case "videoCourse": return CourseItem.fromVideo(source); // example for future
                // case "pdfCourse": return CourseItem.fromPdf(source);
                default:
                    throw new IllegalArgumentException("Unknown content type: " + type);
            }
        }
    }

    /**
     * A small immutable DTO used by the adapter UI layer.
     * Keeps the adapter decoupled from the concrete model details.
     */
    private static class CourseItem {
        private final String postId;
        private final String title;
        private final long price;
        private final String description;
        private final String duration;
        private final String thumbnail;
        private final String postedBy;
        private final String introvideo;

        private CourseItem(String postId, String title, long price, String description,
                           String duration, String thumbnail, String postedBy, String introvideo) {
            this.postId = postId;
            this.title = title;
            this.price = price;
            this.description = description;
            this.duration = duration;
            this.thumbnail = thumbnail;
            this.postedBy = postedBy;
            this.introvideo = introvideo;
        }

        static CourseItem from(CourseModel m) {
            if (m == null) {
                return new CourseItem("", "", 0L, "", "", "", "", "");
            }
            return new CourseItem(
                    safe(m.getPostId()),
                    safe(m.getTitle()),
                    m.getPrice(),
                    safe(m.getDescription()),
                    safe(m.getDuration()),
                    safe(m.getThumbnail()),
                    safe(m.getPostedBy()),
                    safe(m.getIntrovideo())
            );
        }

        private static String safe(String s) { return s == null ? "" : s; }

        public String getPostId() { return postId; }
        public String getTitle() { return title; }
        public long getPrice() { return price; }
        public String getDescription() { return description; }
        public String getDuration() { return duration; }
        public String getThumbnail() { return thumbnail; }
        public String getPostedBy() { return postedBy; }
        public String getIntrovideo() { return introvideo; }
    }
}
