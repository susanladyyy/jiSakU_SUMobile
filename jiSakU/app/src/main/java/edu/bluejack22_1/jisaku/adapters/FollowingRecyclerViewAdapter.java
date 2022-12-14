package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.interfaces.RecyclerViewInterface;
import edu.bluejack22_1.jisaku.models.Follow;

public class FollowingRecyclerViewAdapter extends RecyclerView.Adapter<FollowingRecyclerViewAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Follow> follows;
    private RecyclerViewInterface inter;

    public FollowingRecyclerViewAdapter(Context context, ArrayList<Follow> follows, RecyclerViewInterface inter) {
        this.context = context;
        this.follows = follows;
        this.inter = inter;
    }

    @NonNull
    @NotNull
    @Override
    public FollowingRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_follow, parent, false);
        return new FollowingRecyclerViewAdapter.MyViewHolder(view, inter);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowingRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.name.setText(follows.get(position).getName());

        if(follows.get(position).getProfile().equals("default")) {
            holder.profile.setImageResource(R.drawable.ic_default_user_profile);
        }
        else {
            Picasso.get().load(Uri.parse(follows.get(position).getProfile())).into(holder.profile);
        }
    }

    @Override
    public int getItemCount() {
        return follows.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name;

        public MyViewHolder(@NonNull @NotNull View itemView, RecyclerViewInterface inter) {
            super(itemView);

            profile = itemView.findViewById(R.id.profileFollowImage);
            name = itemView.findViewById(R.id.userFollowName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inter != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            inter.OnPostClick(pos);
                        }
                    }
                }
            });
        }

    }
}
