package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Registration extends AppCompatActivity {
    private EditText login, password, nickname;
    private Button register, auth;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);

        login = findViewById(R.id.logAuth);
        nickname = findViewById(R.id.nicknameAuth);
        password = findViewById(R.id.passAuth);
        register = findViewById(R.id.registrationAuth);
        auth = findViewById(R.id.autorizisationAuth);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });

        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void validData(){

        if(!Patterns.EMAIL_ADDRESS.matcher(login.getText().toString().trim()).matches()){
            login.setError("Неверный формат логина");
        }
        else if (TextUtils.isEmpty(login.getText().toString().trim())){
            password.setError("Неверный формат логина");
        }
        else if (TextUtils.isEmpty(password.getText().toString().trim())){
            password.setError("Неверный формат пароля");
        }
        else if (password.getText().toString().trim().length() < 7){
            password.setError("Неверный формат пароля");
        }
        else{
            firebaseRegistration();
        }
    }

    private void firebaseRegistration(){
        mAuth.createUserWithEmailAndPassword(login.getText().toString().trim(), password.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(login.getText().toString().trim(), password.getText().toString().trim(), nickname.getText().toString().trim());
                        addDataToFirestore(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addDataToFirestore(User use) {

        db.collection("users").add(use)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        signInWithEmailAndPassword(login.getText().toString().trim(), password.getText().toString().trim());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void signInWithEmailAndPassword(String email, String password) {
        User user = new User();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    user.setLogin(login.getText().toString().trim());

                    db.collection("users").whereEqualTo("login", user.getLogin())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    getUsersFromFireBase();
                                }
                            });
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUsersFromFireBase(){
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if(user.getLogin().equals(login.getText().toString().trim())) {
                                MainActivity.user = user;
                                Intent intent = new Intent(getApplicationContext(), AllStuff.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}