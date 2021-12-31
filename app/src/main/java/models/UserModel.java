package models;

import com.example.chatapp.R;

public class UserModel {

    String profilePic = "R.drawable.user", userName, userMail, userId, userPassword = "null", recentMessage, about, token;
    long  recentMsgTime;

    public UserModel(String profilePic, String userName, String userMail, String userId, String userPassword, String about) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.userMail = userMail;
        this.userId = userId;
        this.userPassword = userPassword;
        this.about = about;
    }


    // For storing in DB
    public UserModel(String userName, String userMail, String userPassword, String profilePic, String about){

        this.profilePic = profilePic;
        this.userName = userName;
        this.userMail = userMail;
        this.userPassword = userPassword;
        this.about = about;


    }


    public UserModel() {
    }

    // for displaying in chats list and search list
    public UserModel(String userName, String userMail, String profilePic) {
        this.userName = userName;
        this.userMail = userMail;
        this.profilePic = profilePic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAbout() {
        return about;
    }

    public long getRecentMsgTime() {
        return recentMsgTime;
    }

    public void setRecentMsgTime(long recentMsgTime) {
        this.recentMsgTime = recentMsgTime;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

}
