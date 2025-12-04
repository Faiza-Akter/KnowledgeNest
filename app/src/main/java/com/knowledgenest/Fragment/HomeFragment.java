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

import com.knowledgenest.Adapter.CourseAdapter;
import com.knowledgenest.Model.CourseModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;

    private Dialog loadingDialog;
    private ArrayList<CourseModel> courseList;
    private CourseAdapter adapter;

    // Facade instance
    private FirebaseFacade firebaseFacade;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFacade = new FirebaseFacade(); // Using Facade Pattern
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initializeLoadingDialog();
        setupRecyclerView();
        loadCourses();   // Facade + Iterator Pattern

        return binding.getRoot();
    }

    private void initializeLoadingDialog() {
        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void setupRecyclerView() {
        courseList = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvCourse.setLayoutManager(layoutManager);
        adapter = new CourseAdapter(requireContext(), courseList);
        binding.rvCourse.setAdapter(adapter);
    }

    // ================== USING BOTH PATTERNS HERE =====================
    private void loadCourses() {

        firebaseFacade.getCourses(new ValueEventListener() { // FACADE Pattern
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                courseList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            CourseModel model = data.getValue(CourseModel.class);
                            if (model != null) {
                                model.setPostId(data.getKey());

                                if ("false".equalsIgnoreCase(model.getEnable())) {
                                    courseList.add(model);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing course", e);
                        }
                    }
                }

                // =============== ITERATOR PATTERN IMPLEMENTED HERE ===============
                CourseIterator iterator = new CourseIterator(courseList);
                while (iterator.hasNext()) {
                    CourseModel c = iterator.next();
                    Log.d(TAG, "Iterator reading: " + c.getTitle());
                }
                // =================================================================

                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(),
                        "Error loading courses: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
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


    // =====================================================================
    // PATTERN 1: FACADE (INNER CLASS – NO EXTRA FILE NEEDED)
    // =====================================================================
    private class FirebaseFacade {

        // Simplifies Firebase calls → Facade Pattern
        public void getCourses(ValueEventListener listener) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("courses")
                    .addValueEventListener(listener);
        }
    }

    // =====================================================================
    // PATTERN 2: ITERATOR (INNER CLASS – NO EXTRA FILE NEEDED)
    // =====================================================================
    private class CourseIterator implements Iterator<CourseModel> {

        private final ArrayList<CourseModel> list;
        private int index = 0;

        public CourseIterator(ArrayList<CourseModel> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public CourseModel next() {
            return list.get(index++);
        }
    }
}
