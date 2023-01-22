package edu.bluejack22_1.jisaku.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.index.qual.LengthOf;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.bluejack22_1.jisaku.LoggedActivity;
import edu.bluejack22_1.jisaku.ProfileSettingActivity;
import edu.bluejack22_1.jisaku.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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

    final int VIDEO_PICK_REQ = 9;
    Uri videoUri;
    VideoView videoPost;
    String filePath;
    static boolean init = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference;
        storageReference = storage.getReference();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        Map<String, String> config = new HashMap<>();
        Intent loggedIntent = getActivity().getIntent();

        EditText titleInput, captionInput;
        Spinner category, complexity;
        Button postButton, uploadVideo;

        titleInput = view.findViewById(R.id.titlePostInput);
        captionInput = view.findViewById(R.id.captionPostInput);
        category = view.findViewById(R.id.categorySpinner);
        complexity = view.findViewById(R.id.complexitySpinner);
        postButton = view.findViewById(R.id.buttonPost);
        uploadVideo = view.findViewById(R.id.uploadVideoButton);
        videoPost = view.findViewById(R.id.postVideoView);

        final String[] categoryChosen = {"Choose Category"};
        final String[] complexityChosen = {"Choose Complexity"};
        String[] categories = getActivity().getResources().getStringArray(R.array.category_array);
        ArrayAdapter categoryArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryArrayAdapter);

        String[] complexities = getActivity().getResources().getStringArray(R.array.complexity_array);
        ArrayAdapter complexityArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, complexities);
        complexityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        complexity.setAdapter(complexityArrayAdapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.categorySpinner) {
                    categoryChosen[0] = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        complexity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getId() == R.id.complexitySpinner) {
                    complexityChosen[0] = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideoChooser();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String title = titleInput.getText().toString();
                String caption = captionInput.getText().toString();

                // validate upload video

                if(videoUri == null) {
                    Toast.makeText(getContext(), R.string.video_post, Toast.LENGTH_LONG).show();
                }
                else if(title.length() <= 0) {
                    Toast.makeText(getContext(), R.string.title_post, Toast.LENGTH_LONG).show();
                }
                else if(caption.length() <= 0) {
                    Toast.makeText(getContext(), R.string.caption_post, Toast.LENGTH_LONG).show();
                }
                else if(categoryChosen[0].equals("Choose Category")) {
                    Toast.makeText(getContext(), R.string.category_post, Toast.LENGTH_LONG).show();
                }
                else if(complexityChosen[0].equals("Choose Complexity")) {
                    Toast.makeText(getContext(), R.string.complexity_post, Toast.LENGTH_LONG).show();
                }
                // post
                else {
                    StorageReference ref = storageReference.child("videos/" + UUID.randomUUID().toString());

                    if(!videoUri.toString().contains("https")) {
                        ref.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String download = uri.toString();

                                            String docId = getActivity().getIntent().getStringExtra("current_user_doc_id");
                                            Map<String, Object> post = new HashMap<>();
                                            post.put("title", title);
                                            post.put("caption", caption);
                                            post.put("videoPath", download);
                                            post.put("category", categoryChosen[0]);
                                            post.put("complexity", complexityChosen[0]);
                                            post.put("userid", docId);
                                            post.put("date", LocalDate.now().toString());

                                            db.collection("posts").add(post);

                                            Intent getData = getActivity().getIntent();

                                            String authEmail = getData.getStringExtra("current_auth_email");
                                            String authName = getData.getStringExtra("current_auth_name");
                                            String authBio = getData.getStringExtra("current_auth_bio");
                                            String authProfile = getData.getStringExtra("current_user_profile");
                                            ArrayList<String> authFollower = (ArrayList<String>) getData.getExtras().getSerializable("current_user_follower");
                                            ArrayList<String> authFollowing = (ArrayList<String>) getData.getExtras().getSerializable("current_user_following");

                                            Intent intent = new Intent(getActivity(), LoggedActivity.class);
                                            intent.putExtra("current_user_doc_id", docId);
                                            intent.putExtra("current_auth_email", authEmail);
                                            intent.putExtra("current_auth_name", authName);
                                            intent.putExtra("current_auth_bio", authBio);
                                            intent.putExtra("current_user_profile", authProfile);
                                            intent.putExtra("current_user_follower", authFollower);
                                            intent.putExtra("current_user_following", authFollowing);

                                            String cred = loggedIntent.getStringExtra("credentials");
                                            if(cred != null && cred == "google_signin") {
                                                intent.putExtra("credentials", "google_signin");
                                            }
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
        return view;
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, VIDEO_PICK_REQ);
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(requestCode == VIDEO_PICK_REQ && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();

            filePath = videoUri.getPath();

            MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(videoPost);
            videoPost.setMediaController(mediaController);
            videoPost.setVideoURI(videoUri);
            videoPost.requestFocus();
            videoPost.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    videoPost.pause();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}