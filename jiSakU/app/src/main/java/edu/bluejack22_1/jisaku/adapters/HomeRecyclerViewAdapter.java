package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.models.Post;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<Post> posts;

    public HomeRecyclerViewAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public HomeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_home_post, parent, false);
        return new HomeRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.videoPost.setVideoURI(Uri.parse(posts.get(position).getVideoPath()));
        holder.title.setText(posts.get(position).getTitle());
        holder.caption.setText(posts.get(position).getCaption());
        holder.commentCount.setText(posts.get(position).getComment().size() + "");

        if(posts.get(position).getWishlist().size() == 0) {
            holder.heart.setVisibility(View.INVISIBLE);
            holder.unHeart.setVisibility(View.VISIBLE);
        }
        else {
            holder.heart.setVisibility(View.VISIBLE);
            holder.unHeart.setVisibility(View.INVISIBLE);
        }

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.heart.setVisibility(View.INVISIBLE);
                holder.unHeart.setVisibility(View.VISIBLE);
            }
        });

        holder.unHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.heart.setVisibility(View.VISIBLE);
                holder.unHeart.setVisibility(View.INVISIBLE);

                Map<String, Object> wishesMap = new HashMap<>();
                wishesMap.put("userid", posts.get(position).getCurrdocId());
                wishesMap.put("postid", posts.get(position).getDocId());
                holder.db.collection("wishlists").add(wishesMap);

                holder.db.collection("users").document(posts.get(position).getCurrdocId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        holder.wishes = (ArrayList<String>) task.getResult().get("wishlist");

                        holder.wishes.add(posts.get(position).getDocId());
                        Map<String, Object> wishMap = new HashMap<>();
                        wishMap.put("wishlist", holder.wishes);
                        Log.d("wishes", holder.wishes.toString() + "");
                        holder.db.collection("users").document(posts.get(position).getCurrdocId()).update(wishMap);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        VideoView videoPost;
        TextView title, caption, commentCount;
        ImageView heart, unHeart;
        FirebaseFirestore db;
        ArrayList<String> wishes = new ArrayList<>();

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            db = FirebaseFirestore.getInstance();
            videoPost = itemView.findViewById(R.id.videoViewFollowedPost);
            title = itemView.findViewById(R.id.titlePostHome);
            caption = itemView.findViewById(R.id.captionPostHome);
            commentCount = itemView.findViewById(R.id.commentCountTextView);
            heart = itemView.findViewById(R.id.wishListhome);
            unHeart = itemView.findViewById(R.id.wishListHomeUn);
        }
    }
}