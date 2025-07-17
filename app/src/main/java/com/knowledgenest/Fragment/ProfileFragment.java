package com.knowledgenest.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.knowledgenest.Login.SignInActivity;
import com.knowledgenest.Model.UserModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Dialog loadingDialog;
    Uri profileUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setupLoadingDialog();

        // Apply window insets properly
        ViewCompat.setOnApplyWindowInsetsListener(binding.pf, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadProfileData();

        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        binding.cardTerms.setOnClickListener(v -> {
            showTermsAndConditionsDialog();
        });

        binding.cardRate.setOnClickListener(v -> {
            openPlayStore();
        });

        binding.cardShare.setOnClickListener(v -> {
            shareApp();
        });

        binding.cardLogout.setOnClickListener(v -> {
            logoutUser();
        });

        return binding.getRoot();
    }

    private void showTermsAndConditionsDialog() {
        final Dialog termsDialog = new Dialog(requireContext());
        termsDialog.setContentView(R.layout.terms_dialog);

        // Set dialog window properties
        Window window = termsDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        // Get the TextView and set its content
        TextView termsContent = termsDialog.findViewById(R.id.termsContent);
        termsContent.setText(getString(R.string.terms_and_conditions_content));

        // Set up the agree button
        termsDialog.findViewById(R.id.btnAccept).setOnClickListener(v -> termsDialog.dismiss());

        // Prevent accidental dismissal
        termsDialog.setCancelable(false);
        termsDialog.setCanceledOnTouchOutside(false);

        // Show the dialog
        termsDialog.show();
    }

    private void setupLoadingDialog() {
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }
    }

    private void loadProfileData() {
        database.getReference().child("user_details").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model != null) {
                                binding.userName.setText(model.getName());
                                binding.useEmail.setText(model.getEmail());
                                Picasso.get()
                                        .load(model.getProfile())
                                        .placeholder(R.drawable.userprofile)
                                        .into(binding.profileImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            updateProfile(data.getData());
        }
    }

    private void updateProfile(Uri uri) {
        loadingDialog.show();
        final StorageReference reference = storage.getReference()
                .child("profile_image")
                .child(auth.getCurrentUser().getUid());

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    reference.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("profile", downloadUri.toString());

                                database.getReference()
                                        .child("user_details")
                                        .child(auth.getUid())
                                        .updateChildren(map)
                                        .addOnSuccessListener(unused -> {
                                            loadingDialog.dismiss();
                                            Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                                        });
                            });
                });
    }

    private void shareApp() {
        try {
            String appName = getString(R.string.app_name);
            String shareText = "Check out " + appName + " - a great learning app!\n\n";
            shareText += "Download it from: https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error sharing app", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + requireActivity().getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
        }
    }

    private void logoutUser() {
        auth.signOut();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}