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

public class ProfileDIYRecyclerViewAdapter extends RecyclerView.Adapter<ProfileDIYRecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<Post> posts;

    public ProfileDIYRecyclerViewAdapter(RecyclerViewInterface recyclerViewInterface, Context context, ArrayList<Post> posts) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public ProfileDIYRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_post_diy, parent, false);
        return new ProfileDIYRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProfileDIYRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.DIYVideo.setVideoURI(Uri.parse(posts.get(position).getVideoPath()));
        holder.DIYVideo.requestFocus();
        holder.DIYVideoTitle.setText(posts.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView DIYVideo;
        TextView DIYVideoTitle;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            DIYVideo = itemView.findViewById(R.id.videoViewDIYProfile);
            DIYVideoTitle = itemView.findViewById(R.id.videoProfileDIYTitle);

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
