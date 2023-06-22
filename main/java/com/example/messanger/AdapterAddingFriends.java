package com.example.messanger;

import android.content.Context;
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

public class AdapterAddingFriends extends RecyclerView.Adapter<AdapterAddingFriends.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    private Context context;
    private ArrayList<User> objects;
    public AdapterAddingFriends(Context context, ArrayList<User> objects){
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adding_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User object = objects.get(position);
        try{
            Picasso.with(context).load(object.getPhoto()).into(holder.avatar);
        }
        catch (Exception ex){

        }
        holder.textView.setText(object.getNickname());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.user.getFriends().contains(object.getLogin())){
                    Toast.makeText(context, "Пользователь добавлен в друзья", Toast.LENGTH_SHORT).show();
                }
                else{
                    MainActivity.user.deleteAddingFriend(object.getLogin());
                    updateAddingFriends(MainActivity.user);

                    MainActivity.user.addFriend(object.getLogin());
                    updateFriends(MainActivity.user);

                    object.addFriend(MainActivity.user.getLogin());
                    updateFriends(object);
                }
            }
        });
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.user.deleteAddingFriend(object.getLogin());
                updateAddingFriends(MainActivity.user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button button, button1;
        ImageView avatar;
        ViewHolder(View view){
            super(view);
            avatar = view.findViewById(R.id.avatar);
            textView = view.findViewById(R.id.name);
            button = view.findViewById(R.id.addFriendButton);
            button1 = view.findViewById(R.id.deleteFriendButton);
        }
    }

    private void updateAddingFriends(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("addingFriends", object.getAddingFriends())
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

    private void updateFriends(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("friends", object.getFriends())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Друг добавлен", Toast.LENGTH_SHORT).show();
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
}
