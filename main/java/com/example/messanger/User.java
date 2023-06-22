package com.example.messanger;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String login;
    private String password;
    private String nickname;
    private String photo;
    private ArrayList<String> friends = new ArrayList<String>();
    private ArrayList<String> AddingFriends = new ArrayList<String>();
    private ArrayList<Integer> chatIds = new ArrayList<Integer>();

    public User(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }
    public User() { }
    public String getLogin() {
        return login;
    }
    public void addAddingFriend(String user){
        this.AddingFriends.add(user);
    }
    public ArrayList<String> getAddingFriends(){
        return this.AddingFriends;
    }
    public void setAddingFriends(ArrayList<String> users){
        this.AddingFriends = users;
    }
    public void setFriends(ArrayList<String> users){
        this.friends = users;
    }
    public void setChatIds(ArrayList<Integer> chats){
        this.chatIds = chats;
    }
    public void addChatIds(int chat){
        this.chatIds.add(chat);
    }
    public void deleteAddingFriend(String user){
        this.AddingFriends.remove(user);
    }
    public void addFriend(String user){
        this.friends.add(user);
    }
    public ArrayList<String> getFriends(){
        return this.friends;
    }
    public void deleteFriend(String user){
        this.friends.remove(user);
    }
    public ArrayList<Integer> getChats(){
        return this.chatIds;
    }
    public void deleteChat(Integer chatId){
        this.chatIds.remove(chatId);
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
