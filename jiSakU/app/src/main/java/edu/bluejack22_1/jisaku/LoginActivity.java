package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private SignInButton googleSignInButton;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    String email, password;
    EditText emailLogin, passwordLogin;
    TextView gotoRegister;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        gotoRegister = findViewById(R.id.goToRegisterActivity);

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(registerActivity);
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailLogin.getText().toString();
                password = passwordLogin.getText().toString();

                if(email.length() <= 0){
                    Toast.makeText(getApplicationContext(), "Email must be filled", Toast.LENGTH_LONG).show();
                }
                else if(password.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Password must be filled", Toast.LENGTH_LONG).show();
                }
                else {
                    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();

                            if(check) Toast.makeText(getApplicationContext(), "Email does not exists", Toast.LENGTH_LONG).show();
                            else {
                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    Map<String, Object> currUser = new HashMap<>();
                                                    String docId = "";

                                                    if(task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot doc: task.getResult()) {
                                                            docId = doc.getId();
                                                            currUser = doc.getData();
                                                        }

                                                        Intent loggedActivity = new Intent(LoginActivity.this, LoggedActivity.class);
                                                        loggedActivity.putExtra("current_auth_email", currUser.get("email").toString());
                                                        loggedActivity.putExtra("current_auth_name", currUser.get("name").toString());
                                                        loggedActivity.putExtra("current_user_doc_id", docId);

                                                        if(currUser.get("bio") != null) loggedActivity.putExtra("current_auth_bio", currUser.get("bio").toString());
                                                        else loggedActivity.putExtra("current_auth_bio", "");

                                                        if(currUser.get("profile") != null) loggedActivity.putExtra("current_user_profile", currUser.get("profile").toString());
                                                        else loggedActivity.putExtra("current_user_profile", "");

                                                        if(currUser.get("followers") != null) loggedActivity.putExtra("current_user_follower", (ArrayList) currUser.get("followers"));
                                                        else {
                                                            ArrayList<String> fol = new ArrayList<>();
                                                            loggedActivity.putExtra("current_user_follower", fol);
                                                        }

                                                        if(currUser.get("following") != null) loggedActivity.putExtra("current_user_following",  (ArrayList) currUser.get("following"));
                                                        else {
                                                            ArrayList<String> foll = new ArrayList<>();
                                                            loggedActivity.putExtra("current_user_following", foll);
                                                        }

                                                        loggedActivity.putExtra("credentials", "email_pass");
                                                        finish();
                                                        startActivity(loggedActivity);
                                                    }
                                                }
                                            });
                                        }
                                        else Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 6) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);

                finish();
                Intent loggedActivity = new Intent(this, LoggedActivity.class);

                GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
                db.collection("users").whereEqualTo("email", acc.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", acc.getDisplayName());
                            user.put("email", acc.getEmail());

                            db.collection("users").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                   String docId = task.getResult().getId();

                                    loggedActivity.putExtra("current_user_doc_id", docId);
                                    loggedActivity.putExtra("credentials", "google_signin");
                                    startActivity(loggedActivity);
                                }
                            });
                        }
                        else {
                            db.collection("users").whereEqualTo("email", acc.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    String docId = "";

                                    for (QueryDocumentSnapshot doc: task.getResult()) {
                                        docId = doc.getId();
                                    }

                                    loggedActivity.putExtra("current_user_doc_id", docId);
                                    loggedActivity.putExtra("credentials", "google_signin");
                                    startActivity(loggedActivity);
                                }
                            });
                        }
                        return;
                    }
                });

            } catch (ApiException e) {
                Toast.makeText(this, "Error signing in with google", Toast.LENGTH_LONG).show();
            }
        }
    }
}