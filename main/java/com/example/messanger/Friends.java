package com.example.messanger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Friends extends Fragment {
    Button goToFind, goToAdding;
    RecyclerView friends;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends,
                container, false);

        friends = view.findViewById(R.id.friends);
        goToFind = view.findViewById(R.id.goToGetUsers);
        goToAdding = view.findViewById(R.id.addingUsersList);

        if(MainActivity.user.getFriends() == null)
        {
            Toast.makeText(getContext(), "Список друзей пуст", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ArrayList<User> users = new ArrayList<>();
            for(int i = 0; i < AllStuff.users.size(); i++){
                for(int j = 0; j < MainActivity.user.getFriends().size(); j++)
                    try{
                        if(AllStuff.users.get(i).getLogin().equals(MainActivity.user.getFriends().get(j)))
                            users.add(AllStuff.users.get(i));
                    }
                    catch (Exception ex){}
            }

            friends.setLayoutManager(new LinearLayoutManager(getContext()));
            friends.setHasFixedSize(true);
            AdapterFriends adapter = new AdapterFriends(getContext(), users);
            friends.setAdapter(adapter);
        }

        goToFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GetFriends.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        goToAdding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddingFriends.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}