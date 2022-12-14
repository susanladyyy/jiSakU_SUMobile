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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.adapters.HomeRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.models.Comment;
import edu.bluejack22_1.jisaku.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        RecyclerView homePost = view.findViewById(R.id.homeRecylerView);
        TextView noPost = view.findViewById(R.id.noPostHome);

        db = FirebaseFirestore.getInstance();
        String docId = intent.getStringExtra("current_user_doc_id");

        Log.d("", docId + "");

        db.collection("users").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            ArrayList<String> ids = new ArrayList<>();
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().get("following") != null) {
                        ids = (ArrayList) task.getResult().get("following");

                        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            ArrayList<Post> posts = new ArrayList<>();
                            boolean comment = false;

                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                                if(task.isSuccessful()) {

                                    for (QueryDocumentSnapshot doc: task1.getResult()) {
                                        ArrayList<String> tempWishes = new ArrayList<>();
                                        ArrayList<String> wishes = new ArrayList<>();
                                        comment = false;

                                        if(task.getResult().get("wishlist") != null) {
                                            tempWishes = (ArrayList<String>) task.getResult().get("wishlist");
                                            for (String w: tempWishes) {
                                                if(w.equals(doc.getId())) {
                                                    wishes.add(w);
                                                }
                                            }
                                        }

                                        ArrayList<Comment> comments = new ArrayList<>();
                                        if(doc.get("comments") != null) {
                                            comment = true;
                                            ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) doc.get("comments");

                                            for(int i = 0; i < temp.size(); i++) {
                                                comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                            }
                                        }

                                        if(ids.contains(doc.get("userid").toString()) || doc.get("userid").toString().equals(docId)) {
                                            Post post = new Post(docId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);
                                            posts.add(post);
                                        }
                                    }
                                }

                                HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(getContext(), posts);
                                homePost.setAdapter(adapter);
                                homePost.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                        });
                    }
                    else {
                        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            ArrayList<Post> posts = new ArrayList<>();
                            boolean comment = false;

                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                                if(task.isSuccessful()) {
                                    ArrayList<Comment> comments = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc: task1.getResult()) {
                                        ArrayList<String> tempWishes = new ArrayList<>();
                                        ArrayList<String> wishes = new ArrayList<>();
                                        comment = false;

                                        if(task.getResult().get("wishlist") != null) {
                                            tempWishes = (ArrayList<String>) task.getResult().get("wishlist");
                                            for (String w: tempWishes) {
                                                if(w.equals(doc.getId())) {
                                                    wishes.add(w);
                                                }
                                            }
                                        }

                                        if(doc.get("comments") != null) {
                                            comment = true;
                                            ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) doc.get("comments");

                                            for(int i = 0; i < temp.size(); i++) {
                                                comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                            }
                                        }

                                        if(ids.contains(doc.get("userid").toString()) || doc.get("userid").toString().equals(docId)) {
                                            Post post = new Post(docId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);
                                            posts.add(post);
                                        }
                                    }
                                }

                                if(posts.size() == 0) {
                                    if(noPost != null) noPost.setVisibility(View.VISIBLE);
                                    if(homePost != null) homePost.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    if(noPost != null) noPost.setVisibility(View.INVISIBLE);
                                    if(homePost != null) homePost.setVisibility(View.VISIBLE);
                                    HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(getContext(), posts);
                                    homePost.setAdapter(adapter);
                                    homePost.setLayoutManager(new LinearLayoutManager(getContext()));
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }
}