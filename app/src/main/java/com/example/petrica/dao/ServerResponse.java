package com.example.petrica.dao;

import com.example.petrica.model.Event;
import com.example.petrica.model.Rating;
import com.example.petrica.model.Comment;

import java.util.List;

public class ServerResponse {
    protected int responseCode;
    protected List<Event> eventsList;
    protected List<Rating> ratingList;
    protected List<Comment> reviewsList;

    public static final int RESPONSE_TO_IGNORE = -1;
    public static final int RESPONSE_COMING_EVENT = 0;
    public static final int RESPONSE_REGISTERED_EVENT = 1;
    public static final int RESPONSE_SEARCHED_EVENT_FIRST = 2;
    public static final int RESPONSE_SEARCHED_EVENT_NEXT = 3;
    public static final int RESPONSE_SEARCHED_EVENT_END = 4;
    public static final int RESPONSE_COMMENT_EVENT_OK = 5;
    public static final int RESPONSE_WRITING_COMMENT_OK = 6;
    public static final int RESPONSE_WRITING_COMMENT_ERROR = 7;
    public static final int RESPONSE_WRITING_RATING_OK = 8;
    public static final int RESPONSE_WRITING_RATING_ERROR = 9;
    public static final int RESPONSE_WRITING_EVENT_OK = 10;
    public static final int RESPONSE_WRITING_EVENT_ERROR = 11;
    public static final int RESPONSE_DETAIL_EVENT_OK = 12;
    public static final int RESPONSE_DETAIL_EVENT_ERROR = 21;
    public static final int RESPONSE_REGISTER_OK = 13;
    public static final int RESPONSE_REGISTER_ERROR = 14;
    public static final int RESPONSE_INFO_USER_EVENT_OK = 15;
    public static final int RESPONSE_INFO_USER_EVENT_ERROR = 16;
    public static final int RESPONSE_UNREGISTER_OK = 17;
    public static final int RESPONSE_UNREGISTER_ERROR = 18;
    public static final int RESPONSE_DELETE_RATING_ERROR = 19;
    public static final int RESPONSE_DELETE_RATING_OK = 20;
    public static final int RESPONSE_DELETE_COMMENT_OK = 21;
    public static final int RESPONSE_DELETE_COMMENT_ERROR = 22;
    public static final int RESPONSE_COMMENT_EVENT_ERROR = 23;
    public static final int RESPONSE_DELETE_EVENT_OK = 24;
    public static final int RESPONSE_DELETE_EVENT_ERROR = 25;
    public static final int RESPONSE_COMMENT_EVENT_NEXT_OK = 26;
    public static final int RESPONSE_COMMENT_EVENT_NEXT_ERROR = 27;


    public ServerResponse(int responseCode, List<Event> eventsList, List<Rating> ratingList, List<Comment> reviewsList) {
        this.responseCode = responseCode;
        this.eventsList = eventsList;
        this.ratingList = ratingList;
        this.reviewsList = reviewsList;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    public List<Comment> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(List<Comment> reviewsList) {
        this.reviewsList = reviewsList;
    }
}
