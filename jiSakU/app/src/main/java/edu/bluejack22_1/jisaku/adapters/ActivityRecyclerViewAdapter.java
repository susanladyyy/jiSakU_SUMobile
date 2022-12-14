package edu.bluejack22_1.jisaku.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.models.Activity;

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<ActivityRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Activity> activities;

    public ActivityRecyclerViewAdapter(Context context, ArrayList<Activity> activities) {
        this.context = context;
        this.activities = activities;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_activity, parent, false);
        return new ActivityRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
        LocalDate date = LocalDate.parse(activities.get(position).getDate());

        if(activities.get(position).getType().equals("follow")) holder.typeImage.setImageResource(R.drawable.ic_default_user_profile);
        else if(activities.get(position).getType().equals("comment")) holder.typeImage.setImageResource(R.drawable.ic_baseline_comment_24);
        else if(activities.get(position).getType().equals("wishlist")) holder.typeImage.setImageResource(R.drawable.ic_baseline_favorite_24);
        holder.dateAct.setText(formatter.format(date));
        holder.desc.setText(activities.get(position).getActivity());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView desc, dateAct;
        ImageView typeImage;
        FirebaseFirestore db;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            db = FirebaseFirestore.getInstance();
            desc = itemView.findViewById(R.id.descriptionActivity);
            typeImage = itemView.findViewById(R.id.imageViewActivity);
            dateAct = itemView.findViewById(R.id.dateActivity);
        }

    }
}
