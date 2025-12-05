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
/* ==========================================================
   ADAPTER PATTERN – Image Loader Adapter
   ========================================================== */

interface ImageLoaderAdapter {
    void load(String url, View target, int placeholder);
}

class PicassoImageLoaderAdapter implements ImageLoaderAdapter {
    @Override
    public void load(String url, View target, int placeholder) {
        if (target instanceof android.widget.ImageView) {
            Picasso.get()
                    .load(url)
                    .placeholder(placeholder)
                    .into((android.widget.ImageView) target);
        }
    }
}


/* ==========================================================
   COMMAND PATTERN – Interfaces & Implementations
   ========================================================== */

interface Command {
    void execute();
}

// SHARE COMMAND
class ShareAppCommand implements Command {
    private final Fragment fragment;

    public ShareAppCommand(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute() {
        try {
            String appName = fragment.getString(R.string.app_name);
            String shareText = "Check out " + appName + " - a great learning app!\n\n" +
                    "Download it from: https://play.google.com/store/apps/details?id=" +
                    fragment.requireActivity().getPackageName();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            fragment.startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "Error sharing app", Toast.LENGTH_SHORT).show();
        }
    }
}

// OPEN PLAY STORE COMMAND
class OpenPlayStoreCommand implements Command {
    private final Fragment fragment;

    public OpenPlayStoreCommand(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute() {
        try {
            fragment.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + fragment.requireActivity().getPackageName())));
        } catch (Exception e) {
            fragment.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + fragment.requireActivity().getPackageName())));
        }
    }
}

// LOGOUT COMMAND
class LogoutCommand implements Command {
    private final Fragment fragment;
    private final FirebaseAuth auth;

    public LogoutCommand(Fragment fragment, FirebaseAuth auth) {
        this.fragment = fragment;
        this.auth = auth;
    }

    @Override
    public void execute() {
        auth.signOut();
        Intent intent = new Intent(fragment.getContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        fragment.startActivity(intent);
        fragment.requireActivity().finish();
    }
}

// UPDATE PROFILE COMMAND
class UpdateProfileCommand implements Command {
    private final Fragment fragment;
    private final Uri uri;
    private final Dialog loadingDialog;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    private final FirebaseDatabase database;

    public UpdateProfileCommand(Fragment fragment, Uri uri, Dialog loadingDialog,
                                FirebaseAuth auth, FirebaseStorage storage, FirebaseDatabase database) {
        this.fragment = fragment;
        this.uri = uri;
        this.loadingDialog = loadingDialog;
        this.auth = auth;
        this.storage = storage;
        this.database = database;
    }

    @Override
    public void execute() {
        loadingDialog.show();

        StorageReference reference = storage.getReference()
                .child("profile_image")
                .child(auth.getCurrentUser().getUid());

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("profile", downloadUri.toString());

                            database.getReference()
                                    .child("user_details")
                                    .child(auth.getUid())
                                    .updateChildren(map)
                                    .addOnSuccessListener(unused -> {
                                        loadingDialog.dismiss();
                                        Toast.makeText(fragment.getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                                    });
                        }));
    }
}


/* ==========================================================
   INVOKER – Executes Commands
   ========================================================== */
class CommandInvoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        if (command != null) {
            command.execute();
        }
    }
}


/* ==========================================================
   MAIN FRAGMENT – USING BOTH PATTERNS
   ========================================================== */

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Dialog loadingDialog;

    // Adapter Pattern
    private ImageLoaderAdapter imageLoader;

    // Command Invoker
    private final CommandInvoker invoker = new CommandInvoker();

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Image loader adapter (Picasso wrapped)
        imageLoader = new PicassoImageLoaderAdapter();

        setupLoadingDialog();

        ViewCompat.setOnApplyWindowInsetsListener(binding.pf, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadProfileData();

        // -----------------------------------
        // COMMAND PATTERN USAGE
        // -----------------------------------
        binding.cardShare.setOnClickListener(v -> {
            invoker.setCommand(new ShareAppCommand(this));
            invoker.executeCommand();
        });

        binding.cardRate.setOnClickListener(v -> {
            invoker.setCommand(new OpenPlayStoreCommand(this));
            invoker.executeCommand();
        });

        binding.cardLogout.setOnClickListener(v -> {
            invoker.setCommand(new LogoutCommand(this, auth));
            invoker.executeCommand();
        });

        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        binding.cardTerms.setOnClickListener(v -> showTermsAndConditionsDialog());

        return binding.getRoot();
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

                                // ADAPTER PATTERN → loading image through adapter
                                imageLoader.load(model.getProfile(), binding.profileImage, R.drawable.userprofile);
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
            invoker.setCommand(new UpdateProfileCommand(
                    this,
                    data.getData(),
                    loadingDialog,
                    auth,
                    storage,
                    database
            ));
            invoker.executeCommand();
        }
    }

    private void showTermsAndConditionsDialog() {
        Dialog termsDialog = new Dialog(requireContext());
        termsDialog.setContentView(R.layout.terms_dialog);

        Window window = termsDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        TextView termsContent = termsDialog.findViewById(R.id.termsContent);
        termsContent.setText(getString(R.string.terms_and_conditions_content));
        termsDialog.findViewById(R.id.btnAccept).setOnClickListener(v -> termsDialog.dismiss());
        termsDialog.setCancelable(false);
        termsDialog.setCanceledOnTouchOutside(false);
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
}