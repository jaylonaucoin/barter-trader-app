package com.example.myapplication;

import java.io.Serializable;

public class ItemDescription implements Serializable {
    public static final String TAG = "ItemPosted";
    private String name;
    private String description;
    private String address;
    private String condition;
    private String preference;

    public ItemDescription(){};

    public ItemDescription(String name,String description,String address,String condition,String preference){
        this.name = name;
        this.description = description;
        this.address = address;
        this.condition = condition;
        this.preference = preference;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getAddress() {
        return address;
    }

    public String getCondition() {
        return condition;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPreference() {
        return preference;
    }
}
