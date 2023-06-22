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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    private Context context;
    private ArrayList<User> objects;
    public Adapter(Context context, ArrayList<User> objects){
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
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
                if(object.getFriends().contains(MainActivity.user.getLogin())){
                    Toast.makeText(context, "Пользователь уже добавлен в друзья", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!object.getAddingFriends().contains(MainActivity.user.getLogin())){
                    object.addAddingFriend(MainActivity.user.getLogin());
                    addFriend(object);
                    Toast.makeText(context, "Запрос в друзья отправлен", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(context, "Запрос в друзья уже отправлен", Toast.LENGTH_SHORT).show();
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
        ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.name);
            avatar = view.findViewById(R.id.avatar);
            button = view.findViewById(R.id.addFriendButton);
        }
    }

    private void addFriend(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("addingFriends", object.getAddingFriends())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Запрос в друзья отправлен", Toast.LENGTH_SHORT).show();
                                        objects.remove(object);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Произошла ошибка при отправке запроса", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
