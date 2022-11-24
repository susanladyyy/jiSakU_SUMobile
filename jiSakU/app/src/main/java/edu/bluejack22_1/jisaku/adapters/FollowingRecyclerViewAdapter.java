package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.models.Follow;

public class FollowingRecyclerViewAdapter extends RecyclerView.Adapter<FollowingRecyclerViewAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Follow> follows;

    public FollowingRecyclerViewAdapter(Context context, ArrayList<Follow> follows) {
        this.context = context;
        this.follows = follows;
    }

    @NonNull
    @NotNull
    @Override
    public FollowingRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_view_follow, parent, false);
        return new FollowingRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowingRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.name.setText(follows.get(position).getName());

        if(follows.get(position).getProfile().equals("default")) {
            holder.profile.setImageResource(R.drawable.ic_default_user_profile);
        }
        else {
            holder.profile.setImageURI(Uri.parse(follows.get(position).getProfile()));
        }
    }

    @Override
    public int getItemCount() {
        return follows.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profileFollowImage);
            name = itemView.findViewById(R.id.userFollowName);
        }

    }
}
