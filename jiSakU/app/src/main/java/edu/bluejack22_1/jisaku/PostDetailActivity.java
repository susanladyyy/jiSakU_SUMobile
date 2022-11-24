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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import edu.bluejack22_1.jisaku.models.Comment;

public class PostDetailActivity extends AppCompatActivity {

    TextView postTitle, postCaption, postCategory, postComplexity, poster, datePost, numWishlist, numComments;
    VideoView postVideo;
    FirebaseFirestore db;

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

        Log.d("Video Path", intent.getStringExtra("videoPath")+ " ");
        postVideo.setVideoURI(Uri.parse(intent.getStringExtra("videoPath")));
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(postVideo);
//        postVideo.setMediaController(mediaController);
//        postVideo.requestFocus();
//        postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                postVideo.pause();
//            }
//        });

        postTitle.setText(intent.getStringExtra("title_post"));
        postCaption.setText(intent.getStringExtra("caption_post"));
        postCategory.setText(intent.getStringExtra("category"));
        postComplexity.setText(intent.getStringExtra("complexity"));
        datePost.setText(formatter.format(date));

        db.collection("wishlists").whereEqualTo("postid", intent.getStringExtra("docId_post")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    numWishlist.setText(task.getResult().size() + "");
                }
            }
        });
        db.collection("posts").document(intent.getStringExtra("docId_post")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                String userid = task.getResult().getString("userid");

                if(task.getResult().get("comment") != null) {
                    ArrayList<Map<String, Object>> temp = (ArrayList<Map<String, Object>>) task.getResult().get("comment");
                    numComments.setText(temp.size() + "");
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
}