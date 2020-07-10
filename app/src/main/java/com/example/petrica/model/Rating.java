package com.example.petrica.model;

import com.example.petrica.exceptions.RatingException;

public class Rating {
    // Class representing a rating
    protected String id_user;
    protected String name_user;
    protected int number;
    public static final int MIN_RATE = 0;
    public static final int MAX_RATE = 5;

    public Rating(String id_user, String name_user, int number) throws RatingException {
        this.id_user = id_user;
        this.name_user = name_user;
        setNumber(number);
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) throws RatingException{
        if (number < MIN_RATE || number > MAX_RATE ){
            throw new RatingException("Error : number must be between "+MIN_RATE+" and "+MAX_RATE);
        }
        this.number = number;
    }
}
