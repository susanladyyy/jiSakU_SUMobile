package edu.bluejack22_1.jisaku.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import edu.bluejack22_1.jisaku.R;

import edu.bluejack22_1.jisaku.adapters.ActivityRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.models.Activity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView activityList = view.findViewById(R.id.activityList);
        Intent intent = getActivity().getIntent();

        String userId = intent.getStringExtra("current_user_doc_id");

        getUserActivity(db, userId, activityList);

        return view;
    }

    public void getUserActivity(FirebaseFirestore db, String userId, RecyclerView activityList) {
        final String[] userid = {""};
        final String[] activity = { "" };
        final String[] type = {""};
        final String[] date = {""};
        final String[] postTitle = {""};

        db.collection("activities").whereEqualTo("other", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                ArrayList<Activity> activities = new ArrayList<>();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        final Activity[] act = {null};

                        if(doc.get("userid") != null) userid[0] = doc.get("userid").toString();
                        if(doc.get("postTitle") != null) {
                            postTitle[0] = doc.get("postTitle").toString();
                        }
                        if(doc.get("type") != null) type[0] = doc.get("type").toString();
                        if(doc.get("date") != null) date[0] = doc.get("date").toString();

                        String finalType = type[0];
                        String finalPostTitle = postTitle[0];
                        Log.d("final", finalType);
                        db.collection("users").document(userid[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(finalType.equals("wishlist")) {
                                    activity[0] = getString(R.string.wishlist_act);
                                }
                                else if(finalType.equals("follow")) {
                                    activity[0] = task.getResult().get("name").toString() + getString(R.string.follow_act);
                                }
                                else {
                                    activity[0] = task.getResult().get("name").toString() + " " + getString(R.string.comment_act) + " " + finalPostTitle;
                                }

                                Log.d("act", activity[0] + " activity");
                                act[0] = new Activity(userid[0], activity[0], type[0], date[0]);
                                activities.add(act[0]);

                                ActivityRecyclerViewAdapter adapter = new ActivityRecyclerViewAdapter(getContext(), activities);
                                activityList.setAdapter(adapter);
                                activityList.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                        });
                    }
                }
            }
        });
    }
}