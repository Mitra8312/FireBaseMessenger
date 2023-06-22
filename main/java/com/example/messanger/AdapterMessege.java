package com.example.messanger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMessege extends RecyclerView.Adapter<AdapterMessege.ViewHolder> {
    private Context context;
    private ArrayList<Messenge> objects;
    private ArrayList<User> users = new ArrayList<>();
    public AdapterMessege(Context context, ArrayList<Messenge> objects){
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messege, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messenge object = objects.get(position);
        users = AllStuff.users;
        users.add(MainActivity.user);
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getLogin().equals(object.getSender()))
                holder.textView.setText(users.get(i).getNickname());
        }
        if(object.getLogDel().equals(MainActivity.user.getLogin())) holder.textView1.setText("Вы удалили сообщение");
        else holder.textView1.setText(object.getText());

        holder.cons.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.long_click_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.deleteForMe){
                            object.setLogDel(MainActivity.user.getLogin());
                            return true;
                        }
                        else if(item.getItemId() == R.id.deleteForAll){
                            objects.remove(object);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView, textView1;
        ConstraintLayout cons;
        ViewHolder(View view){
            super(view);
            cons = view.findViewById(R.id.messegeSelf);
            textView = view.findViewById(R.id.name);
            textView1 = view.findViewById(R.id.sender);
        }
    }
}
