package models;

public class MessageModel {

    String uId, msgText;
    long msgTime;

    public MessageModel() {
    }

    public MessageModel(long msgTime, String msgText) {
        this.msgTime = msgTime;
        this.msgText = msgText;
    }

    public MessageModel(String uId, String msgText, long msgTime) {
        this.uId = uId;
        this.msgText = msgText;
        this.msgTime = msgTime;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
