package edu.bluejack22_1.jisaku;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import edu.bluejack22_1.jisaku.fragments.FollowListFragment;
import edu.bluejack22_1.jisaku.fragments.FollowingListFragment;
import edu.bluejack22_1.jisaku.fragments.ProfileFragment;

public class FollowingActivity extends AppCompatActivity {

    String id, email, name, bio = "", profile = "default";
    ArrayList authFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        Intent getInt = getIntent();

        if((ArrayList<String>) getInt.getExtras().getSerializable("following_list") != null) {
            authFollowing = (ArrayList<String>) getInt.getExtras().getSerializable("following_list");
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

        Fragment fragment = new FollowingListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.following_fragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
//            getFragmentManager().popBackStack();
//        }
//        else {
//            super.onBackPressed();
//        }
        Intent intent = new Intent(FollowingActivity.this, LoggedActivity.class);
        intent.putExtra("current_user_doc_id", id);
        intent.putExtra("current_auth_name", name);
        intent.putExtra("current_auth_email", email);
        intent.putExtra("current_auth_bio", bio);
        intent.putExtra("current_user_profile", profile);
        intent.putExtra("current_user_following", authFollowing);
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