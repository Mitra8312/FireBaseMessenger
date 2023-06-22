package com.example.messanger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Messages extends Fragment {

    private Handler handler;
    private int interval = 5000;
    RecyclerView chatiki;
    Button addChat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenges,
                container, false);

        chatiki = view.findViewById(R.id.chatiki);
        addChat = view.findViewById(R.id.addChat);

        handler = new Handler();
        handler.postDelayed(runnable, interval);

        MainActivity.user.getChats().clear();
        for(int j = 0; j < AllStuff.dialogs.size(); j++)
            if(AllStuff.dialogs.get(j).getUsers().contains(MainActivity.user.getLogin()))
                MainActivity.user.addChatIds(AllStuff.dialogs.get(j).getId());

        if(MainActivity.user.getChats() == null)
        {
            Toast.makeText(getContext(), "Список чатов пуст", Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateAdapter();
        }

        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MainActivity.user.getFriends().size() < 1){

                }
                else{
                    ArrayList<String> usersD = new ArrayList<>();
                    usersD.clear();
                    usersD = MainActivity.user.getFriends();
                    usersD.add(MainActivity.user.getLogin());
                    Dialog dialog = new Dialog(usersD);

                    AllStuff.dialogs.add(dialog);

                    addDataToFirestore(dialog);

                    for(int i = 0; i < AllStuff.users.size(); i++){
                        for(int j = 0; j < MainActivity.user.getFriends().size(); j++){
                            if(AllStuff.users.get(i).getLogin().equals(MainActivity.user.getFriends().get(j))){
                                AllStuff.users.get(i).addChatIds(dialog.getId());
                                updateChats(AllStuff.users.get(i));
                            }
                        }
                    }

                    MainActivity.user.addChatIds(dialog.getId());
                    updateChats(MainActivity.user);
                    updateAdapter();
                }
            }
        });

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void updateAdapter(){
        ArrayList<Dialog> chats = new ArrayList<>();
        for(int i = 0; i < AllStuff.dialogs.size(); i++){
            for(int j = 0; j < MainActivity.user.getChats().size(); j++){
                if(AllStuff.dialogs.get(i).getId() == MainActivity.user.getChats().get(j))
                    chats.add(AllStuff.dialogs.get(i));
            }
        }

        chatiki.setLayoutManager(new LinearLayoutManager(getContext()));
        chatiki.setHasFixedSize(true);
        AdapterChats adapter = new AdapterChats(getContext(), chats);
        chatiki.setAdapter(adapter);
    }

    private void addDataToFirestore(Dialog dialog) {
        db.collection("dialogs").add(dialog)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateChats(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("chats", object.getChats())
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateAdapter();
            handler.postDelayed(this, interval);
        }
    };
}