package com.example.petrica.model;

import java.util.Date;

public class Comment {
    // Class representing a comment
    protected Date date;
    protected String id_user;
    protected String name_user;
    protected String message;
    protected String id_comment;

    public Comment(Date date, String id_user, String name_user, String message,String id_comment) {
        this.date = date;
        this.id_user = id_user;
        this.name_user = name_user;
        this.message = message;
        this.id_comment = id_comment;
    }

    public Date getDate() {
        return date;
    }

    public String getId_user() {
        return id_user;
    }

    public String getName_user() {
        return name_user;
    }

    public String getMessage() {
        return message;
    }

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }
}
