package com.example.vsaik.snapchat;

/**
 * Created by vsaik on 12/3/2016.
 */
public class User{

     String nickname;
     String email;
     String profilePicUrl;
     String location;
   String profession;
  String aboutMe;
 String interests;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    String userId;
   String visibilty;

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVisibilty() {
        return visibilty;
    }

    public void setVisibilty(String visibilty) {
        this.visibilty = visibilty;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String phoneNumber;


    public User(){

    }

    public User(String nickname, String email, String profilePicUrl, String location, String profession, String aboutMe, String interests,String userId,String visibilty,String phoneNumber) {
        this.nickname = nickname;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.location = location;
        this.profession = profession;
        this.aboutMe = aboutMe;
        this.interests = interests;
        this.visibilty = visibilty;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }


}


