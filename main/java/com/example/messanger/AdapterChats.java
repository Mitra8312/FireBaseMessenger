package com.example.messanger;

import android.content.Context;
import android.content.Intent;
import android.service.autofill.AutofillService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.ViewHolder> {
    private Context context;
    private ArrayList<Dialog> objects;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    CollectionReference collectionRef1 = db.collection("dialogs");
    public AdapterChats(Context context, ArrayList<Dialog> objects){
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Dialog object = objects.get(position);

        if(object.getUsers().size() > 2){
            holder.textView.setText("Group Chat");
        }
        else{
            for(int i = 0; i < object.getUsers().size(); i++){
                for(int j = 0; j < AllStuff.users.size(); j++)
                    if(!(object.getUsers().get(i).equals(MainActivity.user.getLogin())) && object.getUsers().contains(AllStuff.users.get(j).getLogin()))
                        holder.textView.setText(AllStuff.users.get(j).getNickname());
            }
        }


        holder.openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessegerActivity.chatid = object.getId();
                Intent intent = new Intent(context, MessegerActivity.class);
                context.startActivity(intent);

            }
        });

        holder.deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.user.deleteChat(object.getId());
                for(int i = 0; i < AllStuff.users.size(); i++){
                    if(object.getUsers().contains(AllStuff.users.get(i).getLogin())){
                        AllStuff.users.get(i).deleteChat(object.getId());
                        updateChats(AllStuff.users.get(i));
                    }
                }
                updateChats(MainActivity.user);
                AllStuff.dialogs.remove(object);
                deleteDialog(object);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button openChat;
        Button deleteChat;
        ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.name);
            openChat = view.findViewById(R.id.openChat);
            deleteChat = view.findViewById(R.id.deleteChat);
        }
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
                                        Toast.makeText(context, "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteDialog(Dialog dialog){
        collectionRef1.whereEqualTo("id", dialog.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Удаление успешно", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Произошла ошибка при удалении чата", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
