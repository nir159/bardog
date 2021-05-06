package com.nalamala.bardog;

import java.io.Serializable;

public class Dog implements Serializable {

    String ID;
    String dogName;
    String isImmunized;
    String dogType;
    String ownerName;
    String ownerPhone;
    String ownerAddress;
    String comments;
    String birthDate;
    String profileImage;


    public Dog(String dogName, String isImmunized, String dogType, String ownerName, String ownerPhone, String ownerAddress, String comments, String birthDate, String profileImage, String ID) {
        this.dogName = dogName;
        this.isImmunized = isImmunized;
        this.dogType = dogType;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerAddress = ownerAddress;
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

    public String getdogType() {
        return dogType;
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

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
