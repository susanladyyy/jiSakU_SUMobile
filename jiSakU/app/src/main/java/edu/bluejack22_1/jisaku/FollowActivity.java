package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import edu.bluejack22_1.jisaku.adapters.FollowRecyclerViewAdapter;
import edu.bluejack22_1.jisaku.fragments.FollowListFragment;
import edu.bluejack22_1.jisaku.fragments.ProfileFragment;
import edu.bluejack22_1.jisaku.models.Follow;

public class FollowActivity extends AppCompatActivity {

    String id, email, name, bio = "", profile = "default";
    ArrayList<String> authFollower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Intent getInt = getIntent();

        if(getInt.getExtras().getSerializable("follow_list") != null) {
            authFollower = (ArrayList<String>) getInt.getExtras().getSerializable("follow_list");
        }

        if(getInt.getStringExtra("current_user_doc_id") != null) {
            id = getInt.getStringExtra("current_user_doc_id");
        }
        if(getInt.getStringExtra("current_auth_email") != null) {
            email = getInt.getStringExtra("current_auth_email");
        }
        if(getInt.getStringExtra("current_auth_name") != null) {
            name = getInt.getStringExtra("current_auth_name");
        }
        if(getInt.getStringExtra("current_auth_bio") != null) {
            bio = getInt.getStringExtra("current_auth_bio");
        }
        if(getInt.getStringExtra("current_user_profile") != null) {
            profile = getInt.getStringExtra("current_user_profile");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Fragment fragment = new FollowListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.follow_fragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
//            getFragmentManager().popBackStack();
//        }
//        else {
//            super.onBackPressed();
//        }
        Intent intent = new Intent(FollowActivity.this, LoggedActivity.class);
        intent.putExtra("current_user_doc_id", id);
        intent.putExtra("current_auth_name", name);
        intent.putExtra("current_auth_email", email);
        intent.putExtra("current_auth_bio", bio);
        intent.putExtra("current_user_profile", profile);
        intent.putExtra("current_user_follower", authFollower);
        startActivity(intent);
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