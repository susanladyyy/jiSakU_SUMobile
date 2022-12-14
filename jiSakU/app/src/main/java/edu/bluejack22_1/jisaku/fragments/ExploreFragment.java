package edu.bluejack22_1.jisaku.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import edu.bluejack22_1.jisaku.PostDetailActivity;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.adapters.ExploreRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.interfaces.RecyclerViewInterface;
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
    Boolean s;

    public void filterMenu(View v, RecyclerView exploreRv, String docIdUser, TextView notFound, TextView filteredBy) {
        // kalau mau ambil namanya pakai menu item get title
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.byAll:
                        filteredBy.setText(menuItem.getTitle().toString());
                        viewPosts(exploreRv, docIdUser, notFound);
                        return true;
                    case R.id.category1:
                    case R.id.category2:
                    case R.id.category3:
                    case R.id.category4:
                    case R.id.category5:
                        filteredBy.setText(menuItem.getTitle().toString());
                        viewFilteredPosts("category", menuItem.getTitle().toString(), exploreRv, notFound, docIdUser);
                        return true;
                    case R.id.complexity1:
                    case R.id.complexity2:
                    case R.id.complexity3:
                    case R.id.complexity4:
                        filteredBy.setText(menuItem.getTitle().toString());
                        viewFilteredPosts("complexity", menuItem.getTitle().toString(), exploreRv, notFound, docIdUser);
                        return true;
                }

                return false;
            }
        });

        popup.inflate(R.menu.filter_menu);
        popup.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        db = FirebaseFirestore.getInstance();
        RecyclerView exploreRv = view.findViewById(R.id.exploreRecylerView);
        ImageView filter = view.findViewById(R.id.filterByButton);
        EditText searchInput = view.findViewById(R.id.searchTitleInput);
        TextView notFound = view.findViewById(R.id.notFound);
        TextView filteredBy = view.findViewById(R.id.filteredBy);
        Button searchButton = view.findViewById(R.id.searchButton);
        Intent intent = getActivity().getIntent();
        String docIdUser = intent.getStringExtra("current_user_doc_id");
        s = false;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s = true;
                String searched = searchInput.getText().toString();

                if(!searched.isEmpty()) {
                    viewSearchedPosts(exploreRv, docIdUser, notFound, searchInput);
                }
                else {
                    viewPosts(exploreRv, docIdUser, notFound);
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMenu(view, exploreRv, docIdUser, notFound, filteredBy);
            }
        });

        // set models
        // fill in array list after getting the data from db
        if(s == false) viewPosts(exploreRv, docIdUser, notFound);
        else {
            s = false;

            String searched = searchInput.getText().toString();
            if(searched.isEmpty()) {
                viewPosts(exploreRv, docIdUser, notFound);
            }

        }

        return view;
    }

    public void viewFilteredPosts(String type, String filter, RecyclerView exploreRv, TextView notFound, String docIdUser) {
        if(type.equals("category")) {
            db.collection("posts").whereEqualTo("category", filter).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    ArrayList<Post> posts = new ArrayList<>();
                    boolean comment = false;

                    if(task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            notFound.setVisibility(View.VISIBLE);
                            exploreRv.setVisibility(View.INVISIBLE);
                        }
                        else {
                            notFound.setVisibility(View.INVISIBLE);
                            exploreRv.setVisibility(View.VISIBLE);
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
                                        comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                    }
                                }

                                Post post = new Post(docIdUser, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                posts.add(post);
                            }

                            boolean finalComment = comment;
                            ExploreRecyclerViewAdapter adapter = new ExploreRecyclerViewAdapter(getContext(), posts, new RecyclerViewInterface() {
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
                                    intent.putExtra("current_user_doc_id", docIdUser);

                                    startActivity(intent);
                                }
                            });
                            exploreRv.setAdapter(adapter);
                            exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                }
            });
        }
        else if(type.equals("complexity")) {
            db.collection("posts").whereEqualTo("complexity", filter).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    ArrayList<Post> posts = new ArrayList<>();
                    boolean comment = false;

                    if(task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            notFound.setVisibility(View.VISIBLE);
                            exploreRv.setVisibility(View.INVISIBLE);
                        }
                        else {
                            notFound.setVisibility(View.INVISIBLE);
                            exploreRv.setVisibility(View.VISIBLE);
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
                                        comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                    }
                                }

                                Post post = new Post(docIdUser, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                posts.add(post);
                            }

                            boolean finalComment = comment;
                            ExploreRecyclerViewAdapter adapter = new ExploreRecyclerViewAdapter(getContext(), posts, new RecyclerViewInterface() {
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
                                    intent.putExtra("current_user_doc_id", docIdUser);

                                    startActivity(intent);
                                }
                            });
                            exploreRv.setAdapter(adapter);
                            exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                }
            });
        }
    }

    public void viewSearchedPosts(RecyclerView exploreRv, String docIdUser, TextView notFound, EditText searchInput) {
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                ArrayList<Post> posts = new ArrayList<>();
                boolean comment = false;

                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        if(doc.get("title").toString().contains(searchInput.getText().toString())) {
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
                                    comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                }
                            }

                            Post post = new Post(docIdUser, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                            posts.add(post);
                        }
                    }

                    if(posts.isEmpty()) {
                        notFound.setVisibility(View.VISIBLE);
                        exploreRv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        notFound.setVisibility(View.INVISIBLE);
                        exploreRv.setVisibility(View.VISIBLE);
                        boolean finalComment = comment;
                        ExploreRecyclerViewAdapter adapter = new ExploreRecyclerViewAdapter(getContext(), posts, new RecyclerViewInterface() {
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
                                intent.putExtra("current_user_doc_id", docIdUser);

                                startActivity(intent);
                            }
                        });
                        exploreRv.setAdapter(adapter);
                        exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }
        });
    }

    public void viewPosts(RecyclerView exploreRv, String docIdUser, TextView notFound) {
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                ArrayList<Post> posts = new ArrayList<>();
                boolean comment = false;

                if(task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        notFound.setVisibility(View.VISIBLE);
                        exploreRv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        notFound.setVisibility(View.INVISIBLE);
                        exploreRv.setVisibility(View.VISIBLE);
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
                                    comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                }
                            }

                            Post post = new Post(docIdUser, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                            posts.add(post);
                        }

                        boolean finalComment = comment;
                        ExploreRecyclerViewAdapter adapter = new ExploreRecyclerViewAdapter(getContext(), posts, new RecyclerViewInterface() {
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
                                intent.putExtra("current_user_doc_id", docIdUser);

                                startActivity(intent);
                            }
                        });
                        exploreRv.setAdapter(adapter);
                        exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
            }
        });
    }

}