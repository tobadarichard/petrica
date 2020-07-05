package com.example.petrica.model;

import java.util.Date;

public class Review {
    // Class representing a review
    protected Date date;
    protected String id_user;
    protected String name_user;
    protected String message;

    public Review(Date date, String id_user, String name_user, String message) {
        this.date = date;
        this.id_user = id_user;
        this.name_user = name_user;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
