package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.models.Comment;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;

    public CommentRecyclerViewAdapter(Context context, ArrayList comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_comment, parent, false);
        return new CommentRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.db.collection("users").document(comments.get(position).getUserid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    holder.userName.setText(task.getResult().get("name").toString());

                    if(task.getResult().get("profile") != null) {
                        Picasso.get().load(Uri.parse(task.getResult().get("profile").toString())).into(holder.userProfile);
                    }
                }
            }
        });
        holder.userComment.setText(comments.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userProfile;
        TextView userName, userComment;
        FirebaseFirestore db;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            db = FirebaseFirestore.getInstance();
            userProfile = itemView.findViewById(R.id.userProfileComment);
            userName = itemView.findViewById(R.id.userNameComment);
            userComment = itemView.findViewById(R.id.userComment);
        }
    }
}
