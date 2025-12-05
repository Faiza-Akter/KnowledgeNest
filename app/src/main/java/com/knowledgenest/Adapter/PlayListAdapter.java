package com.knowledgenest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.knowledgenest.Model.PlayListModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.RvPlaylistDesignBinding;

import java.util.ArrayList;
import java.util.List;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<PlayListModel> list;
    private final VideoListener listener;

    public PlayListAdapter(Context context, ArrayList<PlayListModel> list, VideoListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_playlist_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayListModel model = list.get(position);
        holder.bind(model, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RvPlaylistDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvPlaylistDesignBinding.bind(itemView);
        }

        public void bind(PlayListModel model, int position) {
            binding.title.setText(model.getTitle());

            // Example composite: one module may contain multiple lessons
            Lesson lesson = new Lesson(model.getTitle(), model.getVideoUrl());
            Module module = new Module("Module " + (position + 1));
            module.add(lesson);

            // Show hierarchy in logs
            module.showContent();

            itemView.setOnClickListener(view ->
                    listener.onClick(position, model.getKey(), model.getVideoUrl(), list.size()));
        }
    }



    // Component Interface
    interface ContentComponent {
        void showContent();
    }

    // Leaf
    static class Lesson implements ContentComponent {
        private final String title;
        private final String videoUrl;

        public Lesson(String title, String videoUrl) {
            this.title = title;
            this.videoUrl = videoUrl;
        }

        @Override
        public void showContent() {
            System.out.println("   Lesson: " + title + " (" + videoUrl + ")");
        }
    }

    // Composite
    static class Module implements ContentComponent {
        private final String moduleName;
        private final List<ContentComponent> components = new ArrayList<>();

        public Module(String moduleName) {
            this.moduleName = moduleName;
        }

        public void add(ContentComponent component) {
            components.add(component);
        }

        @Override
        public void showContent() {
            System.out.println("Module: " + moduleName);
            for (ContentComponent c : components) {
                c.showContent();
            }
        }
    }


    public interface VideoListener {
        void onClick(int position, String key, String videoUrl, int size);
    }
}
