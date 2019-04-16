package com.example.icecreamtruckv2.chat;

public class ChatMessage {

    private int mIcon;
    private String mName;
    private String mId;
    private String mMessage;

    public ChatMessage() {
        // empty constructor
    }

    public int getIcon() {
        return mIcon;
    }
    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }

    public String getMessage() {
        return mMessage;
    }
    public void setMessage(String message) {
        mMessage = message;
    }
}
