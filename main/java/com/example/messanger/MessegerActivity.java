package com.example.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MessegerActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("dialogs");
    public static int chatid;
    private Dialog dialog;
    TextView toUser;
    RecyclerView recyclerView;
    Button send;
    EditText messege;
    private Handler handler;
    private int interval = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messeger);

        handler = new Handler();
        handler.postDelayed(runnable, interval);

        toUser = findViewById(R.id.toUser);
        recyclerView = findViewById(R.id.recyclerView2);
        send = findViewById(R.id.send);
        messege = findViewById(R.id.editText);
        messege.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        for(int i = 0; AllStuff.dialogs.size() > i; i++){
            if(AllStuff.dialogs.get(i).getId() == chatid){
                dialog = AllStuff.dialogs.get(i);
            }
        }

        if(dialog.getUsers().size() > 2){
            StringBuilder a = new StringBuilder();
            for(int i = 0; i < dialog.getUsers().size(); i++)
                a.append(dialog.getUsers().get(i) + " ");
            toUser.setText(a.toString());
        }
        else{
            for(int i = 0; dialog.getUsers().size() > i; i++)
                if(!dialog.getUsers().get(i).equals(MainActivity.user.getLogin()))
                    toUser.setText(dialog.getUsers().get(i));
        }

        if(dialog.getMesseges() == null){

        }
        else{
            updateRecycle();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messege.getText().toString().trim().isEmpty()){

                }
                else{
                    Messenge messenge = new Messenge(MainActivity.user.getLogin(), messege.getText().toString());
                    dialog.addMesseges(messenge);
                    updateMesseges();
                    updateRecycle();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateRecycle();
            handler.postDelayed(this, interval);
        }
    };

    private void updateRecycle(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        AdapterMessege adapter = new AdapterMessege(getApplicationContext(), dialog.getMesseges());
        recyclerView.setAdapter(adapter);
    }
    private void updateMesseges(){
        collectionRef.whereEqualTo("id", dialog.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("messeges", dialog.getMesseges())
                                    .addOnSuccessListener(aVoid -> {
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}