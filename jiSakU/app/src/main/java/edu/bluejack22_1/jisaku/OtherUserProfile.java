package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.adapters.ProfileDIYRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.adapters.WishlistRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.interfaces.RecyclerViewInterface;
import edu.bluejack22_1.jisaku.models.Comment;
import edu.bluejack22_1.jisaku.models.Post;

public class OtherUserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseFirestore db;

        TextView username, bio, follower, following, noPost, noWish;
        CircleImageView profile;
        RecyclerView diyListProfile, wishListProfile;
        Button followButton, unfollowButton;

        Intent intent = getIntent();

        db = FirebaseFirestore.getInstance();
        username = findViewById(R.id.usernameTextViewOther);
        bio = findViewById(R.id.bioTextviewOther);
        follower = findViewById(R.id.followerCountOther);
        following = findViewById(R.id.followingCountOther);
        profile = findViewById(R.id.circleImageViewOther);
        noPost = findViewById(R.id.noPostTextOther);
        noWish = findViewById(R.id.noWishlistTextOther);
        diyListProfile = findViewById(R.id.DIYListProfileOther);
        wishListProfile = findViewById(R.id.WishListProfileOther);
        followButton = findViewById(R.id.followButton);
        unfollowButton = findViewById(R.id.unfollowButton);

        String currUserId = intent.getStringExtra("current_user_doc_id");
        String userId = "";

        if(intent.getStringExtra("clicked_follower_id") != null) {
            userId = intent.getStringExtra("clicked_follower_id");
        }
        else {
            userId = intent.getStringExtra("clicked_following_id");
        }

        final String[] name = {""};

        db.collection("users").document(currUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    name[0] = task.getResult().get("name").toString();
                }
            }
        });

        String finalUserId1 = userId;
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfollowButton.setVisibility(View.VISIBLE);
                followButton.setVisibility(View.INVISIBLE);

                db.collection("users").document(finalUserId1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList<String> fol = new ArrayList<>();

                        if(task.getResult().get("followers") != null) {
                            fol = (ArrayList<String>) task.getResult().get("followers");
                        }

                        fol.add(currUserId);

                        Map<String, Object> folUpd = new HashMap<>();
                        folUpd.put("followers", fol);
                        db.collection("users").document(finalUserId1).update(folUpd);

                        Map<String, Object> activity = new HashMap<>();
                        activity.put("userid", finalUserId1);
                        activity.put("date", LocalDate.now().toString());
                        activity.put("activity", name[0] + " started following you");
                        activity.put("type", "follow");

                        db.collection("activities").add(activity);

                        follower.setText(fol.size() + "");
                    }
                });

                db.collection("users").document(currUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList<String> fol = new ArrayList<>();

                        if(task.getResult().get("following") != null) {
                            fol = (ArrayList<String>) task.getResult().get("following");
                        }

                        fol.add(finalUserId1);

                        Map<String, Object> folUpd = new HashMap<>();
                        folUpd.put("following", fol);

                        db.collection("users").document(currUserId).update(folUpd);
                    }
                });
            }
        });

        String finalUserId2 = userId;
        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfollowButton.setVisibility(View.INVISIBLE);
                followButton.setVisibility(View.VISIBLE);

                db.collection("users").document(finalUserId2).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList<String> fol = new ArrayList<>();

                        if(task.getResult().get("followers") != null) {
                            fol = (ArrayList<String>) task.getResult().get("followers");
                        }

                        int rem = fol.indexOf(currUserId);

                        fol.remove(rem);

                        Map<String, Object> folUpd = new HashMap<>();
                        folUpd.put("followers", fol);
                        db.collection("users").document(finalUserId2).update(folUpd);

                        follower.setText(fol.size() + "");
                    }
                });

                db.collection("users").document(currUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList<String> fol = new ArrayList<>();

                        if(task.getResult().get("following") != null) {
                            fol = (ArrayList<String>) task.getResult().get("following");
                        }

                        int rem = fol.indexOf(finalUserId2);

                        fol.remove(rem);

                        Map<String, Object> folUpd = new HashMap<>();
                        folUpd.put("following", fol);
                        db.collection("users").document(currUserId).update(folUpd);
                    }
                });
            }
        });

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String finalUserId = userId;
        db.collection("users").document(currUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().get("following") != null) {
                        ArrayList<String> followings = (ArrayList<String>) task.getResult().get("following");

                        if(followings.contains(finalUserId)) {
                            unfollowButton.setVisibility(View.VISIBLE);
                            followButton.setVisibility(View.INVISIBLE);
                        }
                        else {
                            unfollowButton.setVisibility(View.INVISIBLE);
                            followButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    username.setText(task.getResult().get("name").toString());
                    if(task.getResult().get("bio") == null) {
                        bio.setText("");
                    }
                    else {
                        bio.setText(task.getResult().get("bio").toString());
                    }

                    if(task.getResult().get("profile") != null) {
                        Picasso.get().load(Uri.parse(task.getResult().get("profile").toString())).into(profile);
                    }

                    if(task.getResult().get("followers") != null) {
                        ArrayList<String> fol = (ArrayList<String>) task.getResult().get("followers");
                        follower.setText(fol.size() + "");
                    }
                    else {
                        follower.setText("0");
                    }

                    if(task.getResult().get("following") != null) {
                        ArrayList<String> foll = (ArrayList<String>) task.getResult().get("following");
                        following.setText(foll.size() + "");
                    }
                    else {
                        following.setText("0");
                    }

                    db.collection("wishlists").whereEqualTo("userid", task.getResult().getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task2) {
                            if(task2.isSuccessful()) {
                                if(task2.getResult().isEmpty()) {
                                    noWish.setVisibility(View.VISIBLE);
                                    wishListProfile.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    noWish.setVisibility(View.INVISIBLE);
                                    wishListProfile.setVisibility(View.VISIBLE);
                                    ArrayList<String> ids = new ArrayList<>();
                                    final boolean[] comment = {false};

                                    for (QueryDocumentSnapshot doc: task2.getResult()) {
                                        ids.add(doc.get("postid").toString());
                                    }

                                    db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task3) {
                                            ArrayList<Post> wishlists = new ArrayList<>();

                                            if(task3.isSuccessful()) {
                                                for (QueryDocumentSnapshot doc: task3.getResult()) {
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

                                                        Post post = new Post(currUserId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                                        wishlists.add(post);
                                                    }

                                                    WishlistRecyclerViewAdapter wishAdapter = new WishlistRecyclerViewAdapter(getApplicationContext(), wishlists);
                                                    wishListProfile.setAdapter(wishAdapter);
                                                    wishListProfile.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                    db.collection("posts").whereEqualTo("userid", task.getResult().getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task1) {
                            if(task1.isSuccessful()) {
                                if(task1.getResult().isEmpty()) {
                                    noPost.setVisibility(View.VISIBLE);
                                    diyListProfile.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    noPost.setVisibility(View.INVISIBLE);
                                    diyListProfile.setVisibility(View.VISIBLE);

                                    boolean comment = false;
                                    ArrayList<Post> posts = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc: task1.getResult()) {
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

                                        Post post = new Post(currUserId, doc.getId(), doc.getString("title"), doc.getString("caption"), doc.getString("complexity"), doc.getString("category"), doc.getString("videoPath"), doc.getString("userid"), doc.getString("date"), comments, wishes);

                                        posts.add(post);
                                    }

                                    boolean finalComment = comment;
                                    ProfileDIYRecyclerViewAdapter adapter = new ProfileDIYRecyclerViewAdapter(new RecyclerViewInterface() {
                                        @Override
                                        public void OnPostClick(int position) {
                                            Intent intent = new Intent(OtherUserProfile.this, PostDetailActivity.class);

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
                                    }, getApplicationContext(), posts);
                                    diyListProfile.setAdapter(adapter);
                                    diyListProfile.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}