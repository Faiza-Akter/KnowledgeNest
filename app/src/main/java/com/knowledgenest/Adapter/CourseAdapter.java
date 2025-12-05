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

        CourseModel raw = courseList.get(position);

        // Proper Factory Pattern (enum + interface + concrete product)
        ContentItem item = ContentFactory.create(ContentType.COURSE, raw);

        holder.bind(item);
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


    // ============================================================
    //                  FACTORY PATTERN IMPLEMENTATION
    // ============================================================

    // 1) Product type enum
    private enum ContentType {
        COURSE
    }

    // 2) Product interface (supertype)
    private interface ContentItem {
        String getPostId();
        String getTitle();
        long getPrice();
        String getDescription();
        String getDuration();
        String getThumbnail();
        String getPostedBy();
        String getIntrovideo();
    }

    // 3) Concrete Product (CourseItem)
    private static class CourseItem implements ContentItem {

        private final String postId;
        private final String title;
        private final long price;
        private final String description;
        private final String duration;
        private final String thumbnail;
        private final String postedBy;
        private final String introvideo;

        CourseItem(CourseModel m) {
            this.postId = safe(m.getPostId());
            this.title = safe(m.getTitle());
            this.price = m.getPrice();
            this.description = safe(m.getDescription());
            this.duration = safe(m.getDuration());
            this.thumbnail = safe(m.getThumbnail());
            this.postedBy = safe(m.getPostedBy());
            this.introvideo = safe(m.getIntrovideo());
        }

        private static String safe(String s) {
            return s == null ? "" : s;
        }

        @Override public String getPostId() { return postId; }
        @Override public String getTitle() { return title; }
        @Override public long getPrice() { return price; }
        @Override public String getDescription() { return description; }
        @Override public String getDuration() { return duration; }
        @Override public String getThumbnail() { return thumbnail; }
        @Override public String getPostedBy() { return postedBy; }
        @Override public String getIntrovideo() { return introvideo; }
    }

    // 4) Factory Class
    private static class ContentFactory {
        static ContentItem create(ContentType type, CourseModel model) {
            switch (type) {
                case COURSE:
                    return new CourseItem(model);

                default:
                    throw new IllegalArgumentException("Unknown ContentType: " + type);
            }
        }
    }


    // ============================================================
    //                        VIEW HOLDER
    // ============================================================

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvCourseDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvCourseDesignBinding.bind(itemView);
        }

        public void bind(ContentItem course) {

            loadThumbnail(course.getThumbnail());

            binding.courseTitle.setText(course.getTitle());
            binding.coursePrice.setText("$" + course.getPrice());

            loadCreatorInfo(course.getPostedBy());

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

        private void openCourseDetails(ContentItem course) {
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