package com.example.messanger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFriends extends RecyclerView.Adapter<AdapterFriends.ViewHolder> {
    private Context context;
    private ArrayList<User> objects;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");

    public AdapterFriends(Context context, ArrayList<User> objects){
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User object = objects.get(position);
        holder.textView.setText(object.getNickname());

        try{
            Picasso.with(context).load(object.getPhoto()).into(holder.avatar);
        }
        catch (Exception ex){

        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> users = new ArrayList<>();
                users.add(MainActivity.user.getLogin());
                users.add(object.getLogin());

                ArrayList<String> users1 = new ArrayList<>();
                users1.add(object.getLogin());
                users1.add(MainActivity.user.getLogin());

                for(int i = 0; AllStuff.dialogs.size() > i; i++){
                    if(AllStuff.dialogs.get(i).getUsers().equals(users) || AllStuff.dialogs.get(i).getUsers().equals(users1)){
                        MessegerActivity.chatid = AllStuff.dialogs.get(i).getId();
                        Intent intent = new Intent(context, MessegerActivity.class);
                        context.startActivity(intent);
                        return;
                    }
                }

                Dialog dialog = new Dialog(users);
                AllStuff.dialogs.add(dialog);
                MessegerActivity.chatid = dialog.getId();
                MainActivity.user.addChatIds(dialog.getId());
                object.addChatIds(dialog.getId());
                updateChats(object);
                updateChats(MainActivity.user);
                addDataToFirestore(dialog);
                Intent intent = new Intent(context, MessegerActivity.class);
                context.startActivity(intent);

            }
        });

        holder.deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.user.deleteFriend(object.getLogin());
                object.deleteFriend(MainActivity.user.getLogin());
                updateFriends(MainActivity.user);
                updateFriends(object);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button button;
        ImageView avatar;
        Button deleteFriend;
        ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.name);
            button = view.findViewById(R.id.startChat);
            deleteFriend = view.findViewById(R.id.deleteFriend);
            avatar = view.findViewById(R.id.avatar);
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
    private void updateFriends(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("friends", object.getFriends())
                                    .addOnSuccessListener(aVoid -> {
                                        objects.remove(object);
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
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
