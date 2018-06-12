package com.example.voicelock;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by Sujin Jeong on 2018-05-03.
 */




public class RecyclerItem {

    private int id, bluetooth;
    private String name,position, address;
    private int image, isOpen;


    public RecyclerItem(int id, String name, String position, int image, int isOpen, String address) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.image = image;
        this.isOpen=isOpen;
        this.address=address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public int getIsOpen(){return isOpen;}
    public void setIsOpen(int isOpen){this.isOpen=isOpen;}
    public String getAddress(){return this.address;}
    public void setBle(int ble){this.bluetooth=ble;}
    public int getBle(){return this.bluetooth;}
}
