package edu.bluejack22_1.jisaku.fragments;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.FollowActivity;
import edu.bluejack22_1.jisaku.FollowingActivity;
import edu.bluejack22_1.jisaku.LoginActivity;
import edu.bluejack22_1.jisaku.PostDetailActivity;
import edu.bluejack22_1.jisaku.ProfileSettingActivity;
import edu.bluejack22_1.jisaku.R;
import edu.bluejack22_1.jisaku.adapters.ProfileDIYRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.adapters.WishlistRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.interfaces.RecyclerViewInterface;
import edu.bluejack22_1.jisaku.models.Comment;
import edu.bluejack22_1.jisaku.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseFirestore db;

        TextView username, bio, follower, following, noPost, noWish;
        ImageView setting, logoutButton;
        CircleImageView profile;
        RecyclerView diyListProfile, wishListProfile;

        username = view.findViewById(R.id.usernameTextView);
        bio = view.findViewById(R.id.bioTextview);
        follower = view.findViewById(R.id.followerCount);
        following = view.findViewById(R.id.followingCount);
        profile = view.findViewById(R.id.circleImageView);
        setting = view.findViewById(R.id.settingButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        noPost = view.findViewById(R.id.noPostText);
        noWish = view.findViewById(R.id.noWishlistText);
        diyListProfile = view.findViewById(R.id.DIYListProfile);
        wishListProfile = view.findViewById(R.id.WishListProfile);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        db = FirebaseFirestore.getInstance();

        Intent credentials = getActivity().getIntent();
        String cred = credentials.getStringExtra("credentials");

        if(cred != null && cred.equals("google_signin") && googleAccount != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                }
            });

            Log.d("Google Sign In ", "This is with google sign in");
            Log.d("Display name ", googleAccount.getDisplayName() + "");

            username.setText(googleAccount.getDisplayName());
            Picasso.get().load(googleAccount.getPhotoUrl()).into(profile);

            db.collection("users").whereEqualTo("email", googleAccount.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    Map<String, Object> currUser = new HashMap<>();
                    String docId = "";

                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc: task.getResult()) {
                            docId = doc.getId();
                            currUser = doc.getData();
                        }

                        String bioText = "";
                        if(currUser.get("bio") != null) {
                            bio.setText(currUser.get("bio").toString());
                            bioText = currUser.get("bio").toString();
                        }
                        else {
                            bio.setText("");
                            bioText = "";
                        }

                        if(currUser.get("followers") != null) {
                            follower.setText(((ArrayList<?>) currUser.get("followers")).size() + "");
                        }
                        else {
                            follower.setText("0");
                        }

                        if(currUser.get("following") != null) {
                            following.setText(((ArrayList<?>) currUser.get("following")).size() + "");
                        }
                        else {
                            following.setText("0");
                        }

                        String finalDocId = docId;
                        String finalBioText = bioText;

                        follower.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), FollowActivity.class);
                                db.collection("users").document(finalDocId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            if(task.getResult().get("followers") != null) {
                                                intent.putExtra("follow_list", (ArrayList) task.getResult().get("followers"));
                                            }
                                            else intent.putExtra("follow_list", new ArrayList<String>());
                                            intent.putExtra("current_user_doc_id", finalDocId);
                                        }

                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                        following.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), FollowingActivity.class);
                                db.collection("users").document(finalDocId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()) {
                                            if(task.isSuccessful()) {
                                                if(task.getResult().get("following") != null) {
                                                    intent.putExtra("following_list", (ArrayList) task.getResult().get("following"));
                                                }
                                                else intent.putExtra("following_list", new ArrayList<String>());
                                                intent.putExtra("current_user_doc_id", finalDocId);
                                            }
                                        }
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                        setting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileSettingActivity = new Intent(view.getContext(), ProfileSettingActivity.class);
                                profileSettingActivity.putExtra("current_email_setting", googleAccount.getEmail());
                                profileSettingActivity.putExtra("current_user_doc_id", finalDocId);
                                profileSettingActivity.putExtra("current_user_profile", googleAccount.getPhotoUrl());
                                profileSettingActivity.putExtra("current_auth_name", googleAccount.getDisplayName());
                                profileSettingActivity.putExtra("current_auth_bio", finalBioText);
//                                profileSettingActivity.putExtra("current_user_follower", follower);
//                                profileSettingActivity.putExtra("current_user_following", follo);

                                startActivity(profileSettingActivity);
                            }
                        });

                        String finalDocId1 = docId;
                        db.collection("posts").whereEqualTo("userid", finalDocId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                ArrayList<Post> posts = new ArrayList<>();
                                boolean comment = false;
                                if(task.isSuccessful()) {
                                    if(task.getResult().isEmpty()) {
                                        noPost.setVisibility(View.VISIBLE);
                                        diyListProfile.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        noPost.setVisibility(View.INVISIBLE);
                                        diyListProfile.setVisibility(View.VISIBLE);
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

                                            Post post = new Post(finalDocId1, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                            posts.add(post);
                                        }

                                        boolean finalComment = comment;
                                        ProfileDIYRecyclerViewAdapter adapter = new ProfileDIYRecyclerViewAdapter(new RecyclerViewInterface() {
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
                                        }, getContext(), posts);
                                        diyListProfile.setAdapter(adapter);
                                        diyListProfile.setLayoutManager(new LinearLayoutManager(getContext()));
                                    }
                                }

                            }
                        });

                        db.collection("wishlists").whereEqualTo("userid", docId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(task.getResult().isEmpty()) {
                                        noWish.setVisibility(View.VISIBLE);
                                        wishListProfile.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        noWish.setVisibility(View.INVISIBLE);
                                        wishListProfile.setVisibility(View.VISIBLE);
                                        ArrayList<String> ids = new ArrayList<>();
                                        final boolean[] comment = {false};

                                        for (QueryDocumentSnapshot doc: task.getResult()) {
                                            ids.add(doc.get("postid").toString());
                                        }

                                        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                                                ArrayList<Post> wishlists = new ArrayList<>();

                                                if(task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot doc: task1.getResult()) {
                                                        if(ids.contains(doc.getId())) {
                                                            ArrayList<String> wishes = new ArrayList<>();
                                                            comment[0] = false;
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
                                                                comment[0] = true;
                                                                ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) doc.get("comment");

                                                                for(int i = 0; i < temp.size(); i++) {
                                                                    comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                                                }
                                                            }

                                                            Post post = new Post(finalDocId1, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                                            wishlists.add(post);
                                                        }

                                                        WishlistRecyclerViewAdapter wishAdapter = new WishlistRecyclerViewAdapter(getContext(), wishlists);
                                                        wishListProfile.setAdapter(wishAdapter);
                                                        wishListProfile.setLayoutManager(new LinearLayoutManager(getContext()));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
        else {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });

            Intent authUser = getActivity().getIntent();
            String authEmail = authUser.getStringExtra("current_auth_email");
            String docId = authUser.getStringExtra("current_user_doc_id");
            String authName = authUser.getStringExtra("current_auth_name");
            String authBio = authUser.getStringExtra("current_auth_bio");
            String authProfile = authUser.getStringExtra("current_user_profile");
            ArrayList<String> authFollower = (ArrayList<String>) authUser.getExtras().getSerializable("current_user_follower");
            ArrayList<String> authFollowing = (ArrayList<String>) authUser.getExtras().getSerializable("current_user_following");

            username.setText(authName);
            bio.setText(authBio);

            // kalau ada follower/ following count dulu
            if(authFollower != null) {
                follower.setText(authFollower.size()+"");
            }
            else {
                follower.setText("0");
            }

            if(authFollowing != null) {
                following.setText(authFollowing.size()+"");
            }
            else {
                following.setText("0");
            }

            if(authProfile == null || authProfile.equals("")) {
                profile.setImageResource(R.drawable.ic_default_user_profile);
            }
            // else ambil yang dari db
            else Picasso.get().load(Uri.parse(authProfile)).into(profile);

            follower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FollowActivity.class);
                    db.collection("users").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                if(task.getResult().get("followers") != null) {
                                    intent.putExtra("follow_list", (ArrayList) task.getResult().get("followers"));
                                }
                                else intent.putExtra("follow_list", new ArrayList<String>());
                                intent.putExtra("current_user_doc_id", docId);
                            }

                            startActivity(intent);
                        }
                    });
                }
            });

            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FollowingActivity.class);
                    db.collection("users").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                if(task.isSuccessful()) {
                                    if(task.getResult().get("following") != null) {
                                        intent.putExtra("following_list", (ArrayList) task.getResult().get("following"));
                                    }
                                    else intent.putExtra("following_list", new ArrayList<String>());
                                    intent.putExtra("current_user_doc_id", docId);
                                }
                            }
                            startActivity(intent);
                        }
                    });
                }
            });

            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileSettingActivity = new Intent(view.getContext(), ProfileSettingActivity.class);
                    profileSettingActivity.putExtra("current_email_setting", authEmail);
                    profileSettingActivity.putExtra("current_user_doc_id", docId);
                    profileSettingActivity.putExtra("current_user_profile", authProfile);
                    profileSettingActivity.putExtra("current_auth_name", authName);
                    profileSettingActivity.putExtra("current_auth_bio", authBio);
                    profileSettingActivity.putExtra("current_user_follower", authFollower);
                    profileSettingActivity.putExtra("current_user_following", authFollowing);

                    startActivity(profileSettingActivity);
                }
            });

            db.collection("posts").whereEqualTo("userid", docId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    ArrayList<Post> posts = new ArrayList<>();
                    boolean comment = false;

                    if(task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            noPost.setVisibility(View.VISIBLE);
                            diyListProfile.setVisibility(View.INVISIBLE);
                        }

                        else {
                            noPost.setVisibility(View.INVISIBLE);
                            diyListProfile.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                ArrayList<String> wishes = new ArrayList<>();
                                comment = false;
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

                                Post post = new Post(docId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                posts.add(post);
                            }

                            boolean finalComment = comment;
                            ProfileDIYRecyclerViewAdapter adapter = new ProfileDIYRecyclerViewAdapter(new RecyclerViewInterface() {
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
                            }, getContext(), posts);
                            diyListProfile.setAdapter(adapter);
                            diyListProfile.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }

                }
            });

            db.collection("wishlists").whereEqualTo("userid", docId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            noWish.setVisibility(View.VISIBLE);
                            wishListProfile.setVisibility(View.INVISIBLE);
                        }
                        else {
                            noWish.setVisibility(View.INVISIBLE);
                            wishListProfile.setVisibility(View.VISIBLE);
                            ArrayList<String> ids = new ArrayList<>();
                            final boolean[] comment = {false};

                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                ids.add(doc.get("postid").toString());
                            }

                            db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                                    ArrayList<Post> wishlists = new ArrayList<>();

                                    if(task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc: task1.getResult()) {
                                            if(ids.contains(doc.getId())) {
                                                ArrayList<String> wishes = new ArrayList<>();
                                                comment[0] = false;
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
                                                    comment[0] = true;
                                                    ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) doc.get("comment");

                                                    for(int i = 0; i < temp.size(); i++) {
                                                        comments.add(new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString()));
                                                    }
                                                }

                                                Post post = new Post(docId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                                wishlists.add(post);
                                            }

                                            WishlistRecyclerViewAdapter wishAdapter = new WishlistRecyclerViewAdapter(getContext(), wishlists);
                                            wishListProfile.setAdapter(wishAdapter);
                                            wishListProfile.setLayoutManager(new LinearLayoutManager(getContext()));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        return view;
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
    }
}