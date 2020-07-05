package com.example.petrica.model;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class Event {
    // Class representing an event
    protected Date date;
    protected String description;
    protected String id_organiser;
    protected String name_organiser;
    protected String image_path;
    protected String name;
    protected String theme;

    public Event(Date date, String description, String id_organiser, String name_organiser, String image_path, String name, String theme) {
        this.date = date;
        this.description = description;
        this.id_organiser = id_organiser;
        this.name_organiser = name_organiser;
        this.image_path = image_path;
        this.name = name;
        this.theme = theme;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId_organiser() {
        return id_organiser;
    }

    public void setId_organiser(String id_organiser) {
        this.id_organiser = id_organiser;
    }

    public String getName_organiser() {
        return name_organiser;
    }

    public void setName_organiser(String name_organiser) {
        this.name_organiser = name_organiser;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public static Event getFrom(QueryDocumentSnapshot doc){
        return new Event(doc.getDate("date"), doc.getString("description"), doc.getString("id_organiser")
                , doc.getString("name_organiser"), doc.getString("image_path")
                , doc.getString("name"), doc.getString("theme"));
    }

}
