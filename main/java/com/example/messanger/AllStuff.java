package com.example.messanger;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.messanger.databinding.ActivityAllStuffBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllStuff extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    CollectionReference collectionRef1 = db.collection("dialogs");
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Dialog> dialogs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stuff);

        getUsersFromFireBase();
        getDialogsFromFireBase();

        bottomNavigationView = findViewById(R.id.button_navigation);
        setFragment(new Profile());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navigation_profile){
                    setFragment(new Profile());
                    return true;
                }
                else if (item.getItemId() == R.id.navigation_messeges) {
                    setFragment(new Messages());
                    return true;
                }
                else if (item.getItemId() == R.id.navigation_friends){
                   setFragment(new Friends());
                   return true;
                }
                else return false;
            }
        });
    }
    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, fragment, null)
                .commit();
    }

    private void getUsersFromFireBase(){
        collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    users.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            user.setAddingFriends(documentSnapshot.toObject(User.class).getAddingFriends());
                            user.setFriends(documentSnapshot.toObject(User.class).getFriends());
                            if(!user.getLogin().equals(MainActivity.user.getLogin())) users.add(user);
                            else{
                                MainActivity.user = user;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getDialogsFromFireBase(){
        collectionRef1.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dialogs.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            Dialog dialog = documentSnapshot.toObject(Dialog.class);
                            dialog.setId(documentSnapshot.toObject(Dialog.class).getId());
                            dialogs.add(dialog);
                        }
                    }
                    for(int i = 0; i < dialogs.size(); i++){
                        Dialog.ID = dialogs.get(i).getId() + 1;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}