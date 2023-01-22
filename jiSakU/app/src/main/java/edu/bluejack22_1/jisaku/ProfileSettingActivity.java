package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack22_1.jisaku.fragments.ProfileFragment;

public class ProfileSettingActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQ = 1;
    private Uri imageUri;
    private String name, bio;

    private EditText nameInput, bioInput;
    private Button uploadButton, saveButton;
    private CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();

        Intent fromProfileSet = getIntent();

        nameInput = findViewById(R.id.nameUpdateInput);
        nameInput.setText(fromProfileSet.getStringExtra("current_auth_name"));

        bioInput = findViewById(R.id.bioUpdateInput);
        bioInput.setText(fromProfileSet.getStringExtra("current_auth_bio"));

        ArrayList<String> authFollower = (ArrayList<String>) fromProfileSet.getExtras().getSerializable("current_user_follower");
        ArrayList<String> authFollowing = (ArrayList<String>) fromProfileSet.getExtras().getSerializable("current_user_following");

        profile = findViewById(R.id.circleImageProfileSetting);
        if(fromProfileSet.getStringExtra("current_user_profile") == null || fromProfileSet.getStringExtra("current_user_profile").equals("")) {
            profile.setImageResource(R.drawable.ic_default_user_profile);
        }
        else {
            imageUri = Uri.parse(fromProfileSet.getStringExtra("current_user_profile"));
            Picasso.get().load(Uri.parse(fromProfileSet.getStringExtra("current_user_profile"))).into(profile);
        }

        uploadButton = findViewById(R.id.uploadProfileButton);
        saveButton = findViewById(R.id.saveChangesButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameInput.getText().toString();
                bio = bioInput.getText().toString();
                Intent fromProfile = getIntent();
                String docId = fromProfile.getStringExtra("current_user_doc_id");

                Log.d("Doc ID ", docId);

                if(name.length() <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.name_regis, Toast.LENGTH_LONG).show();
                }
                else {
                    if (imageUri != null) {
                        // upload to storage
                        StorageReference ref = storageReference.child("profiles/" + UUID.randomUUID().toString());

                        if(!imageUri.toString().contains("https")) {
                            ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String download = uri.toString();

                                                Map<String, Object> authUpdate = new HashMap<>();
                                                authUpdate.put("name", name);
                                                if(bio.length() > 0) {
                                                    authUpdate.put("bio", bio);
                                                }
                                                authUpdate.put("profile", download);

                                                db.collection("users").document(docId).update(authUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        Intent backToProfile = new Intent(ProfileSettingActivity.this, LoggedActivity.class);
                                                        backToProfile.putExtra("current_auth_name", name);
                                                        backToProfile.putExtra("current_auth_bio", bio);
                                                        backToProfile.putExtra("current_user_profile", download);
                                                        backToProfile.putExtra("current_user_doc_id", docId);
                                                        if(authFollower != null && authFollowing != null) {
                                                            backToProfile.putExtra("current_user_follower", authFollower);
                                                            backToProfile.putExtra("current_user_following", authFollowing);
                                                        }

                                                        finish();
                                                        startActivity(backToProfile);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else {
                            Map<String, Object> authUpdate = new HashMap<>();
                            authUpdate.put("name", name);
                            if (bio.length() > 0) {
                                authUpdate.put("bio", bio);
                            }
                            authUpdate.put("profile", imageUri.toString());

                            db.collection("users").document(docId).update(authUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Intent backToProfile = new Intent(ProfileSettingActivity.this, LoggedActivity.class);
                                    backToProfile.putExtra("current_auth_name", name);
                                    backToProfile.putExtra("current_auth_bio", bio);
                                    backToProfile.putExtra("current_user_profile", imageUri.toString());
                                    backToProfile.putExtra("current_user_doc_id", docId);
                                    backToProfile.putExtra("current_user_follower", authFollower);
                                    backToProfile.putExtra("current_user_following", authFollowing);

                                    startActivity(backToProfile);
                                }
                            });
                        }
                    }
                    else {
                        Map<String, Object> authUpdate = new HashMap<>();
                        authUpdate.put("name", name);
                        if(bio.length() > 0) {
                            authUpdate.put("bio", bio);
                        }

                        db.collection("users").document(docId).update(authUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                Intent backToProfile = new Intent(ProfileSettingActivity.this, LoggedActivity.class);
                                backToProfile.putExtra("current_auth_name", name);
                                backToProfile.putExtra("current_auth_bio", bio);
                                backToProfile.putExtra("current_user_profile", "");
                                backToProfile.putExtra("current_user_doc_id", docId);
                                backToProfile.putExtra("current_user_follower", authFollower);
                                backToProfile.putExtra("current_user_following", authFollowing);

                                startActivity(backToProfile);
                            }
                        });
                    }
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQ);
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profile);
        }
    }
}