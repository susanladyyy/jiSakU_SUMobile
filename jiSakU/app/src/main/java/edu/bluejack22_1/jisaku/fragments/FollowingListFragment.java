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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.adapters.FollowRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.adapters.FollowingRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.models.Follow;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingListFragment newInstance(String param1, String param2) {
        FollowingListFragment fragment = new FollowingListFragment();
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
        View view = inflater.inflate(R.layout.fragment_following_list, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getActivity().getIntent();

        TextView noFollow = view.findViewById(R.id.noFollowings);
        RecyclerView followRv = view.findViewById(R.id.followingRecylerView);
        ArrayList<String> ids = (ArrayList) intent.getExtras().getSerializable("following_list");

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                ArrayList<Follow> follows = new ArrayList<>();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Log.d("IDs ", ids.get(0) + "");
                        Log.d("DOC ID", doc.getId() + "");
                        Log.d("STATUS", ids.contains("kAP4yIEYM0XP7inMgCBQ") + "");
                        if(ids.contains(doc.getId())) {
                            Log.d("DOC ID", "DOC ID == IDS[ctr]");
                            if(doc.get("profile") != null) {
                                follows.add(new Follow(doc.get("name").toString(), doc.get("profile").toString()));
                            }
                            else {
                                follows.add(new Follow(doc.get("name").toString(), "default"));
                            }
                        }
                    }

                    if(follows.size() == 0) {
                        noFollow.setVisibility(View.VISIBLE);
                        followRv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        noFollow.setVisibility(View.INVISIBLE);
                        followRv.setVisibility(View.VISIBLE);
                    }

                    FollowingRecyclerViewAdapter adapter = new FollowingRecyclerViewAdapter(getContext(), follows);
                    followRv.setAdapter(adapter);
                    followRv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        });

        return view;
    }
}