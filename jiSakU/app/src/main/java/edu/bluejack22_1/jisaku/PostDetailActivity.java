package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.bluejack22_1.jisaku.adapters.CommentRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.models.Comment;

public class PostDetailActivity extends AppCompatActivity {

    TextView postTitle, postCaption, postCategory, postComplexity, poster, datePost, numWishlist, numComments, inputComment;
    VideoView postVideo;
    FirebaseFirestore db;
    RecyclerView commentList;
    ArrayList<Comment> comments;
    ImageView wishlist, nonWishlist;
    Button submitComment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        db = FirebaseFirestore.getInstance();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
        LocalDate date = LocalDate.parse(intent.getStringExtra("date_post"));

        postTitle = findViewById(R.id.titlePostFromDB);
        postCaption = findViewById(R.id.captionPostFromDB);
        postCategory = findViewById(R.id.categoryPostFromDB);
        postComplexity = findViewById(R.id.complexityPostFromDB);
        postVideo = findViewById(R.id.videoViewPostDetail);
        poster = findViewById(R.id.posterName);
        datePost = findViewById(R.id.dateOfThePost);
        numWishlist = findViewById(R.id.numOfSaves);
        numComments = findViewById(R.id.numOfComments);
        commentList = findViewById(R.id.recylerViewComment);
        comments = new ArrayList<>();
        wishlist = findViewById(R.id.wishList);
        nonWishlist = findViewById(R.id.nonWishList);
        inputComment = findViewById(R.id.commentTextField);
        submitComment = findViewById(R.id.submitCommentButton);

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = inputComment.getText().toString();

                if(comment.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Comment must be filled", Toast.LENGTH_LONG).show();
                }
                else {
                    String userId = intent.getStringExtra("current_user_doc_id");
                    String postId = intent.getStringExtra("docId_post");

                    db.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                ArrayList<Map<String, Object>> comments;

                                if(task.getResult().get("comments") == null) {
                                    comments = new ArrayList<>();
                                }
                                else {
                                    comments = (ArrayList<Map<String, Object>>) task.getResult().get("comments");

                                }

                                Map<String, Object> temp = new HashMap<>();
                                temp.put("userid", userId);
                                temp.put("comment", comment);
                                comments.add(temp);

                                Log.d("comments", comments + "");
                                Map<String, Object> com = new HashMap<>();
                                com.put("comments", comments);

                                db.collection("posts").document(postId).update(com);
                            }
                        }
                    });
                }
            }
        });

        Log.d("Video Path", intent.getStringExtra("videoPath")+ " ");
        postVideo.setVideoURI(Uri.parse(intent.getStringExtra("videoPath")));
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(postVideo);
        postVideo.setMediaController(mediaController);
        postVideo.requestFocus();
        postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                postVideo.pause();
            }
        });

        postTitle.setText(intent.getStringExtra("title_post"));
        postCaption.setText(intent.getStringExtra("caption_post"));
        postCategory.setText(intent.getStringExtra("category"));
        postComplexity.setText(intent.getStringExtra("complexity"));
        datePost.setText(formatter.format(date));

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postId = intent.getStringExtra("docId_post");
                String userId = intent.getStringExtra("current_user_doc_id");

                db.collection("wishlists").whereEqualTo("postid", postId).whereEqualTo("userid", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                db.collection("wishlists").document(doc.getId()).delete();
                            }
                        }
                    }
                });

                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList<String> wishes = (ArrayList<String>) task.getResult().get("wishlist");
                        int i = 0;

                        if(wishes != null) {
                            for (String wish: wishes) {
                                if(wish.equals(postId)) {
                                    wishes.remove(i);
                                    break;
                                }
                                i++;
                            }

                            Map<String, Object> wishMap = new HashMap<>();
                            wishMap.put("wishlist", wishes);
                            Log.d("wishes", wishes.toString() + "");
                            db.collection("users").document(userId).update(wishMap);

                            updateWishlisted(intent);
                            wishlisted(intent);
                        }
                    }
                });
            }
        });

        nonWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postId = intent.getStringExtra("docId_post");
                String userId = intent.getStringExtra("current_user_doc_id");

                Map<String, Object> wishesMap = new HashMap<>();
                wishesMap.put("userid", userId);
                wishesMap.put("postid", postId);
                db.collection("wishlists").add(wishesMap);

                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        ArrayList wishes = (ArrayList<String>) task.getResult().get("wishlist");

                        wishes.add(postId);
                        Map<String, Object> wishMap = new HashMap<>();
                        wishMap.put("wishlist", wishes);
                        Log.d("wishes", wishes.toString() + "");
                        db.collection("users").document(userId).update(wishMap);

                        Map<String, Object> activity = new HashMap<>();
                        String poster = intent.getStringExtra("userid");
                        activity.put("userid", poster);
                        activity.put("activity", "Your post was added to another user wishlist!");
                        activity.put("date", LocalDate.now().toString());

                        db.collection("activities").add(activity);
                        updateWishlisted(intent);
                        wishlisted(intent);
                    }
                });
            }
        });

        updateWishlisted(intent);
        wishlisted(intent);

        db.collection("posts").document(intent.getStringExtra("docId_post")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                String userid = task.getResult().getString("userid");

                if(task.getResult().get("comments") != null) {
                    showComments(task);
                }

                db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        poster.setText(task.getResult().getString("name"));
                    }
                });
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

    public void updateWishlisted(Intent intent) {
        db.collection("wishlists").whereEqualTo("postid", intent.getStringExtra("docId_post")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    numWishlist.setText(task.getResult().size() + "");
                }
            }
        });
    }

    public void showComments(Task<DocumentSnapshot> task) {
        ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) task.getResult().get("comments");
        numComments.setText(temp.size() + "");

        for(int i = 0; i < temp.size(); i++) {
            Comment com = new Comment(temp.get(i).get("userid").toString(), temp.get(i).get("comment").toString());
            comments.add(com);
        }

        CommentRecyclerViewAdapter commentAdapter = new CommentRecyclerViewAdapter(getApplicationContext(), comments);
        commentList.setAdapter(commentAdapter);
        commentList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void wishlisted(Intent intent) {
        String id = intent.getStringExtra("current_user_doc_id");

        db.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> wishes = (ArrayList<String>) task.getResult().get("wishlist");
                    String postId = intent.getStringExtra("docId_post");

                    if(wishes != null) {
                        if(wishes.contains(postId)) {
                            wishlist.setVisibility(View.VISIBLE);
                            nonWishlist.setVisibility(View.INVISIBLE);
                        }
                        else {
                            wishlist.setVisibility(View.INVISIBLE);
                            nonWishlist.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        wishlist.setVisibility(View.INVISIBLE);
                        nonWishlist.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

}