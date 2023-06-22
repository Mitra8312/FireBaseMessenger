package com.example.messanger;

import java.util.ArrayList;

public class Messenge {
    private String sender;
    private String text;
    private String logDel = "";

    public Messenge(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public Messenge(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLogDel() {
        return logDel;
    }

    public void setLogDel(String logDel) {
        this.logDel = logDel;
    }
}
