package com.nalamala.bardog;

import java.io.Serializable;

public class Dog implements Serializable {

    String ID;
    String dogName;
    String isImmunized;
    String desc;
    String ownerName;
    String comments;
    String birthDate;
    String ownerPhone;
    String profileImage;


    public Dog(String dogName, String isImmunized, String desc, String ownerName, String ownerPhone, String comments, String birthDate, String profileImage, String ID) {
        this.dogName = dogName;
        this.isImmunized = isImmunized;
        this.desc = desc;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.comments = comments;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getDogName() {
        return dogName;
    }

    public String getIsImmunized() {
        return isImmunized;
    }

    public String getDesc() {
        return desc;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getComments() {
        return comments;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
