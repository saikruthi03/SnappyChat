package com.example.vsaik.snapchat;

import android.graphics.Bitmap;

/**
 * Created by vsaik on 12/3/2016.
 */
public class UserDetails {

    public static String userName;
    public static String email;
    public static String profilePicUrl;
    public static String location=" ";
    public static String profession=" ";
    public static String aboutMe=" ";
    public static String interests =" ";
    public static String userId;
    public static String visibilty=" ";
    public static Bitmap image;

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        UserDetails.fullName = fullName;
    }

    public static String provider;
    public static String fullName;

    public static String getProvider() {
        return provider;
    }

    public static void setProvider(String provider) {
        UserDetails.provider = provider;
    }



    public static Bitmap getImage() {
        return image;
    }

    public static void setImage(Bitmap image) {
        UserDetails.image = image;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        UserDetails.location = location;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {

        UserDetails.userName = userName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserDetails.email = email;
    }

    public static String getProfilePicUrl() {
        return profilePicUrl;
    }

    public static void setProfilePicUrl(String profilePicUrl) {
        UserDetails.profilePicUrl = profilePicUrl;
    }

    public static String getProfession() {
        return profession;
    }

    public static void setProfession(String profession) {
        UserDetails.profession = profession;
    }

    public static String getInterests() {
        return interests;
    }

    public static void setInterests(String interests) {
        UserDetails.interests = interests;
    }

    public static String getAboutMe() {
        return aboutMe;
    }

    public static void setAboutMe(String aboutMe) {
        UserDetails.aboutMe = aboutMe;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        UserDetails.userId = userId;
    }

    public static String getVisibilty() {
        return visibilty;
    }

    public static void setVisibilty(String visibilty) {
        UserDetails.visibilty = visibilty;
    }


    public UserDetails(){

    }

    public UserDetails(String nickname, String email, String profilePicUrl, String location, String profession, String aboutMe, String interests,String userId,String visibilty) {
        this.userName = nickname;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.location = location;
        this.profession = profession;
        this.aboutMe = aboutMe;
        this.interests = interests;
        this.visibilty = visibilty;
        this.userId = userId;
    }
}







