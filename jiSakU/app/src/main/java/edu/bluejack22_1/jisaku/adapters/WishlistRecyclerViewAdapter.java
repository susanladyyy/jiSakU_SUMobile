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
import edu.bluejack22_1.jisaku.models.Post;

public class WishlistRecyclerViewAdapter extends RecyclerView.Adapter<WishlistRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Post> wishlists;

    public WishlistRecyclerViewAdapter(Context context, ArrayList<Post> wishlists) {
        this.context = context;
        this.wishlists = wishlists;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_wishlist, parent, false);
        return new WishlistRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.wishlistVideo.setVideoURI(Uri.parse(wishlists.get(position).getVideoPath()));
        holder.wishlistVideo.requestFocus();
        holder.wishlistVideoTitle.setText(wishlists.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return wishlists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView wishlistVideo;
        TextView wishlistVideoTitle;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            wishlistVideo = itemView.findViewById(R.id.wishListVideosProfile);
            wishlistVideoTitle = itemView.findViewById(R.id.wishListVideoTitle);
        }
    }
}
