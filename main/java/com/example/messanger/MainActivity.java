package com.example.messanger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText login, password;
    private Button register, auth;
    public static User user = new User();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        login = findViewById(R.id.logReg);
        password = findViewById(R.id.passReg);
        register = findViewById(R.id.registrationReg);
        auth = findViewById(R.id.autorizisationReg);

        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
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
            signInWithEmailAndPassword(login.getText().toString().trim(), password.getText().toString().trim());
        }
    }
    private void signInWithEmailAndPassword(String email, String password) {
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
                            if(user.getLogin().equals(login.getText().toString().trim())){
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