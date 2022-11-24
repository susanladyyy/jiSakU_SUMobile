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
import edu.bluejack22_1.jisaku.interfaces.RecylerViewInterface;
import edu.bluejack22_1.jisaku.models.Post;

public class ExploreRecyclerViewAdapter extends RecyclerView.Adapter<ExploreRecyclerViewAdapter.MyViewHolder> {

    private final RecylerViewInterface recylerViewInterface;
    private Context context;
    private ArrayList<Post> posts;

    public ExploreRecyclerViewAdapter(Context context, ArrayList<Post> posts, RecylerViewInterface recylerViewInterface) {
        this.context = context;
        this.posts = posts;
        this.recylerViewInterface = recylerViewInterface;
    }

    @NonNull
    @NotNull
    @Override
    public ExploreRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_explore, parent, false);
        return new ExploreRecyclerViewAdapter.MyViewHolder(view, recylerViewInterface);
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

        public MyViewHolder(@NonNull @NotNull View itemView, RecylerViewInterface recylerViewInterface) {
            super(itemView);

            exploreVideo = itemView.findViewById(R.id.videoCardExplore);
            exploreVideoTitle = itemView.findViewById(R.id.textViewTitleVideo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recylerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recylerViewInterface.OnPostClick(pos);
                        }
                    }
                }
            });
        }
    }
}
