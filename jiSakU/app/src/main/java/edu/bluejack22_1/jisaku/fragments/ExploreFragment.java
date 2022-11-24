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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bluejack22_1.jisaku.PostDetailActivity;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.adapters.ExploreRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.interfaces.RecylerViewInterface;
import edu.bluejack22_1.jisaku.models.Comment;
import edu.bluejack22_1.jisaku.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        RecyclerView exploreRv = view.findViewById(R.id.exploreRecylerView);

        db = FirebaseFirestore.getInstance();
        Intent intent = getActivity().getIntent();

        String docIdUser = intent.getStringExtra("current_user_doc_id");
        // set models
        // fill in array list after getting the data from db
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                ArrayList<Post> posts = new ArrayList<>();
                boolean comment = false;

                if(task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {

                    }
                    else {
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            comment = false;

                            ArrayList<String> wishes = new ArrayList<>();
                            db.collection("wishlists").whereEqualTo("postid", doc.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                                    for (QueryDocumentSnapshot doc1: task1.getResult()) {
                                        wishes.add(doc1.get("postid").toString());
                                    }
                                }
                            });

                            ArrayList<Comment> comments = new ArrayList<>();
                            if(doc.get("comment") != null) {
                                comment = true;
                                ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) doc.get("comment");

                                for(int i = 0; i < temp.size(); i++) {
                                    comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString(), temp.get(i).get("postid").toString()));
                                }
                            }

                            Post post = new Post(docIdUser, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                            posts.add(post);
                        }

                        boolean finalComment = comment;
                        ExploreRecyclerViewAdapter adapter = new ExploreRecyclerViewAdapter(getContext(), posts, new RecylerViewInterface() {
                            @Override
                            public void OnPostClick(int position) {
                                Intent intent = new Intent(getActivity(), PostDetailActivity.class);

                                intent.putExtra("docId_post", posts.get(position).getDocId());
                                intent.putExtra("title_post", posts.get(position).getTitle());
                                intent.putExtra("caption_post", posts.get(position).getCaption());
                                intent.putExtra("complexity", posts.get(position).getComplexity());
                                intent.putExtra("category", posts.get(position).getCategory());
                                intent.putExtra("videoPath", posts.get(position).getVideoPath());
                                intent.putExtra("userid", posts.get(position).getUserid());
                                intent.putExtra("date_post", posts.get(position).getDate());

                                startActivity(intent);
                            }
                        });
                        exploreRv.setAdapter(adapter);
                        exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }
        });

        return view;
    }
}