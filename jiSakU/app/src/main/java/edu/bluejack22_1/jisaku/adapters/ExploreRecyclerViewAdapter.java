package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.interfaces.RecyclerViewInterface;
import edu.bluejack22_1.jisaku.models.Post;

public class ExploreRecyclerViewAdapter extends RecyclerView.Adapter<ExploreRecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<Post> posts;

    public ExploreRecyclerViewAdapter(Context context, ArrayList<Post> posts, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.posts = posts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @NotNull
    @Override
    public ExploreRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_explore, parent, false);
        return new ExploreRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExploreRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.exploreVideo.setVideoURI(Uri.parse(posts.get(position).getVideoPath()));
        holder.exploreVideo.requestFocus();
        holder.exploreVideoTitle.setText(posts.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView exploreVideo;
        TextView exploreVideoTitle;

        public MyViewHolder(@NonNull @NotNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            exploreVideo = itemView.findViewById(R.id.videoCardExplore);
            exploreVideoTitle = itemView.findViewById(R.id.textViewTitleVideo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.OnPostClick(pos);
                        }
                    }
                }
            });
        }
    }
}
