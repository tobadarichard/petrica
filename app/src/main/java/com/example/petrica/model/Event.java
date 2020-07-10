package com.example.petrica.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable {
    // Class representing an event
    protected Date date;
    protected String description;
    protected String id_organiser;
    protected String name_organiser;
    protected String image_path;
    protected String name;
    protected String theme;
    protected String id_event;
    protected int num_reg;
    protected int num_rate;
    protected double avg_rate;
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(Date date, String description, String id_organiser, String name_organiser, String image_path, String name,
                 String theme, String id_event, int num_reg, int num_rate, double avg_rate) {
        this.date = date;
        this.description = description;
        this.id_organiser = id_organiser;
        this.name_organiser = name_organiser;
        this.image_path = image_path;
        this.name = name;
        this.theme = theme;
        this.id_event = id_event;
        this.num_reg = num_reg;
        this.num_rate = num_rate;
        this.avg_rate = avg_rate;
    }

    protected Event(Parcel in) {
        date = new Date(in.readLong());
        description = in.readString();
        id_organiser = in.readString();
        name_organiser = in.readString();
        image_path = in.readString();
        name = in.readString();
        theme = in.readString();
        id_event = in.readString();
        num_reg = in.readInt();
        num_rate = in.readInt();
        avg_rate = in.readDouble();
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

    public String getId_event() {
        return id_event;
    }

    public void setId_event(String id_event) {
        this.id_event = id_event;
    }

    public int getNum_reg() {
        return num_reg;
    }

    public void setNum_reg(int num_reg) {
        this.num_reg = num_reg;
    }

    public int getNum_rate() {
        return num_rate;
    }

    public void setNum_rate(int num_rate) {
        this.num_rate = num_rate;
    }

    public double getAvg_rate() {
        return avg_rate;
    }

    public void setAvg_rate(double avg_rate) {
        this.avg_rate = avg_rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date.getTime());
        dest.writeString(description);
        dest.writeString(id_organiser);
        dest.writeString(name_organiser);
        dest.writeString(image_path);
        dest.writeString(name);
        dest.writeString(theme);
        dest.writeString(id_event);
        dest.writeInt(num_reg);
        dest.writeInt(num_rate);
        dest.writeDouble(avg_rate);
    }

    public void updateAvgRating(int oldRate, int newRate) {
        int variation;
        if (newRate == -1 && oldRate == -1){
            return;
        }
        else if (newRate == -1){
            variation = -1;
            newRate = 0;
        }
        else if (oldRate == -1){
            variation = 1;
            oldRate = 0;
        }
        else{
            variation = 0;
        }
        int newNumRate = num_rate + variation;
        double newAvgRate;
        if (newNumRate == 0){
            newAvgRate = 0;
        }
        else{
            newAvgRate = ((avg_rate*num_rate)-oldRate+newRate)/newNumRate;
        }
        avg_rate = newAvgRate;
        num_rate = newNumRate;
    }
}
