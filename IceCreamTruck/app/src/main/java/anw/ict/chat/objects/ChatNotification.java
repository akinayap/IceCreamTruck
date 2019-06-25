package anw.ict.chat.objects;

public class ChatNotification {
    public String sender, type, msg, time;
    public ChatNotification(ChatMessage data) {
        sender = data.getUsername();
        type = data.getType();
        msg = data.getMessage();
        time = data.getTimestamp();
    }
    public ChatNotification(String user, String t, String message, String timestamp) {
        sender = user;
        type = t;
        msg = message;
        time = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
