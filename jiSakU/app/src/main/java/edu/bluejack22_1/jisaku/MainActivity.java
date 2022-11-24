package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String name, email, password, confirmPassword;
    private EditText nameRegister, emailRegister, passwordRegister, confirmPasswordRegister;
    private Button registerButton;
    private TextView gotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        nameRegister = findViewById(R.id.nameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);
        gotoLogin = findViewById(R.id.goToLoginActivity);

        Map<String, Object> users = new HashMap<>();

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(loginActivity);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameRegister.getText().toString();
                email = emailRegister.getText().toString();
                password = passwordRegister.getText().toString();
                confirmPassword = confirmPasswordRegister.getText().toString();

                if(name.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Name must be filled", Toast.LENGTH_LONG).show();
                }
                else if(email.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Email must be filled", Toast.LENGTH_LONG).show();
                }
                else {
                    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();

                            if(check) {
                                if(password.length() <= 0) {
                                    Toast.makeText(getApplicationContext(), "Password must be filled", Toast.LENGTH_LONG).show();
                                }
                                else if(confirmPassword.length() <= 0) {
                                    Toast.makeText(getApplicationContext(), "Confirm password must be filled", Toast.LENGTH_LONG).show();
                                }
                                else if(!password.equals(confirmPassword)) {
                                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    users.put("name", name);
                                    users.put("email", email);
                                    users.put("password", password);

                                    db.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
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

                                                        Intent loggedActivity = new Intent(MainActivity.this, LoggedActivity.class);
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

                                                        finish();
                                                        startActivity(loggedActivity);
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    nameRegister.setText("");
                                    emailRegister.setText("");
                                    passwordRegister.setText("");
                                    confirmPasswordRegister.setText("");
                                }
                            }
                            else Toast.makeText(getApplicationContext(), "Email has been taken", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}