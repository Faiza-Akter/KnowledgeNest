package com.knowledgenest.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knowledgenest.Adapter.CourseAdapter;
import com.knowledgenest.Model.CourseModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Dialog loadingDialog;
    private ArrayList<CourseModel> courseList;
    private CourseAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initializeComponents();
        setupRecyclerView();
        loadCourses();
        return binding.getRoot();
    }

    private void initializeComponents() {
        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    private void setupRecyclerView() {
        courseList = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvCourse.setLayoutManager(layoutManager);
        adapter = new CourseAdapter(requireContext(), courseList);
        binding.rvCourse.setAdapter(adapter);
    }

    private void loadCourses() {
        database.getReference().child("courses") // Changed from "course" to "courses" to match admin app
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                                try {
                                    CourseModel course = courseSnapshot.getValue(CourseModel.class);
                                    if (course != null) {
                                        course.setPostId(courseSnapshot.getKey());
                                        // Only show courses that are enabled
                                        if ("false".equalsIgnoreCase(course.getEnable())) {
                                            courseList.add(course);
                                            Log.d(TAG, "Added course: " + course.getTitle() +
                                                    " | Thumbnail: " + course.getThumbnail());
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing course data", e);
                                }
                            }
                        } else {
                            Log.d(TAG, "No courses found in database");
                        }
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Log.e(TAG, "Database error: " + error.getMessage(), error.toException());
                        Toast.makeText(getContext(), "Failed to load courses: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}