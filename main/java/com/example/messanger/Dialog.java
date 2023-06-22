package com.example.messanger;

import android.provider.CloudMediaProvider;

import java.util.ArrayList;

public class Dialog {
    public static int ID = 0;
    private int id;
    private ArrayList<Messenge> messeges;
    private ArrayList<String> users;

    public Dialog(){
    }
    public Dialog(ArrayList<String> user) {
        ID++;
        this.id = ID;
        this.messeges = new ArrayList<>();
        this.users = user;
    }

    public ArrayList<Messenge> getMesseges() {
        return messeges;
    }
    public void addMesseges(Messenge messege){
        this.messeges.add(messege);
    }

    public void deleteMesseges(Messenge messege){
        this.messeges.remove(messege);
    }

    public void setMesseges(ArrayList<Messenge> messeges) {
        this.messeges = messeges;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
