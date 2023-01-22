package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
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
        View view = inflater.inflate(R.layout.recycler_view_home_post, parent, false);
        return new HomeRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.videoPost.setVideoURI(Uri.parse(posts.get(position).getVideoPath()));
        holder.title.setText(posts.get(position).getTitle());
        holder.caption.setText(posts.get(position).getCaption());

        holder.db.collection("posts").document(posts.get(position).getDocId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().get("comments") != null) {
                        ArrayList<Map<String, Object>> com = (ArrayList<Map<String, Object>>) task.getResult().get("comments");
                        holder.commentCount.setText(com.size() + "");
                    }
                    else {
                        holder.commentCount.setText("0");
                    }
                }
            }
        });

        holder.db.collection("users").document(posts.get(position).getCurrdocId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> wishes = (ArrayList<String>) task.getResult().get("wishlist");
                    String postId = posts.get(position).getDocId();

                    if(wishes != null) {
                        if(wishes.contains(postId)) {
                            holder.heart.setVisibility(View.VISIBLE);
                            holder.unHeart.setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.heart.setVisibility(View.INVISIBLE);
                            holder.unHeart.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        holder.heart.setVisibility(View.INVISIBLE);
                        holder.unHeart.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.heart.setVisibility(View.INVISIBLE);
                holder.unHeart.setVisibility(View.VISIBLE);

                holder.db.collection("wishlists").whereEqualTo("postid", posts.get(position).getDocId()).whereEqualTo("userid", posts.get(position).getCurrdocId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                holder.db.collection("wishlists").document(doc.getId()).delete();
                            }
                        }
                    }
                });

                holder.db.collection("users").document(posts.get(position).getCurrdocId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.getResult().get("wishlist") != null) {
                            holder.wishes = (ArrayList<String>) task.getResult().get("wishlist");
                        }
                        int i = 0;

                        if(holder.wishes != null) {
                            for (String wish: holder.wishes) {
                                if(wish.equals(posts.get(position).getDocId())) {
                                    holder.wishes.remove(i);
                                    break;
                                }
                                i++;
                            }

                            Map<String, Object> wishMap = new HashMap<>();
                            wishMap.put("wishlist", holder.wishes);
                            Log.d("wishes", holder.wishes.toString() + "");
                            holder.db.collection("users").document(posts.get(position).getCurrdocId()).update(wishMap);
                        }
                    }
                });
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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        holder.wishes = (ArrayList<String>) task.getResult().get("wishlist");

                        holder.wishes.add(posts.get(position).getDocId());
                        Map<String, Object> wishMap = new HashMap<>();
                        wishMap.put("wishlist", holder.wishes);
                        Log.d("wishes", holder.wishes.toString() + "");
                        holder.db.collection("users").document(posts.get(position).getCurrdocId()).update(wishMap);

                        Map<String, Object> activity = new HashMap<>();
                        activity.put("userid", posts.get(position).getUserid());
                        activity.put("date", LocalDate.now().toString());
                        activity.put("type", "wishlist");

                        holder.db.collection("activities").add(activity);
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