package com.example.messanger;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {
    private static final int REQUEST_SELECT_PHOTO = 1;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button updateProfile, exit, updatePhoto;
    TextView textView;
    EditText pass, nick, linkPhoto;
    ImageView image;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("users");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,
                container, false);

        updateProfile = view.findViewById(R.id.updateProfile);
        exit = view.findViewById(R.id.exit);
        textView = view.findViewById(R.id.logintext);
        pass = view.findViewById(R.id.newPassword);
        nick = view.findViewById(R.id.newNickname);
        linkPhoto = view.findViewById(R.id.newPhoto);
        updatePhoto = view.findViewById(R.id.updatePhoto);
        image = view.findViewById(R.id.imageProfile);

        try{
            Picasso.with(getContext()).load(MainActivity.user.getPhoto()).into(image);
        }
        catch (Exception ex){

        }

        textView.setText("Ваш логин, который нельзя изменить: " + MainActivity.user.getLogin());
        pass.setHint(MainActivity.user.getPassword());
        nick.setHint(MainActivity.user.getNickname());

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(MainActivity.user);
            }
        });

        updatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linkPhoto.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                }
                else{
                    MainActivity.user.setPhoto(linkPhoto.getText().toString());
                    Picasso.with(getContext()).load(linkPhoto.getText().toString()).into(image);
                    updatePhoto(MainActivity.user);
                }

            }
        });

        return view;
    }

    private void updatePhoto(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            documentRef.update("photo", MainActivity.user.getPhoto())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Фото успешно изменено", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Ошибка обновления фото", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUser(User object){
        collectionRef.whereEqualTo("login", object.getLogin())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            DocumentReference documentRef = documentSnapshot.getReference();
                            if(nick.getText().toString().trim().isEmpty()){

                            }
                            else{
                                documentRef.update("nickname", nick.getText().toString())
                                        .addOnSuccessListener(aVoid -> {
                                            MainActivity.user.setNickname(nick.getText().toString());
                                            Toast.makeText(getContext(), "Псевдоним успешно изменен", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Ошибка обновления псевдонима", Toast.LENGTH_SHORT).show();
                                        });
                            }
                            if(pass.getText().toString().trim().isEmpty()){

                            }
                            else {
                                documentRef.update("password", pass.getText().toString())
                                        .addOnSuccessListener(aVoid -> {

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Ошибка обновления пароля", Toast.LENGTH_SHORT).show();
                                        });
                                user.updatePassword(pass.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    MainActivity.user.setPassword(pass.getText().toString());
                                                    Toast.makeText(getContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            nick.setHint(MainActivity.user.getNickname());
                            pass.setHint(MainActivity.user.getPassword());

                            nick.setText("");
                            pass.setText("");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}