package com.example.messanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AddingFriends extends AppCompatActivity {

    Button back;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_friends);

        recyclerView = findViewById(R.id.addingFriendsList);
        back = findViewById(R.id.backToAllStuff);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllStuff.class);
                startActivity(intent);
                finish();
            }
        });

        ArrayList<User> users = new ArrayList<>();
        for(int i = 0; i < AllStuff.users.size(); i++){
            for(int j = 0; j < MainActivity.user.getAddingFriends().size(); j++)
                if(AllStuff.users.get(i).getLogin().equals(MainActivity.user.getAddingFriends().get(j)))
                    users.add(AllStuff.users.get(i));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        AdapterAddingFriends adapter = new AdapterAddingFriends(this, users);
        recyclerView.setAdapter(adapter);
    }
}