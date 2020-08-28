package com.backend.Memes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Comments")
public class Comments {

    @Id
    private String id;

    private String body;
    private String userPosted;
    private String memeId;

    public Comments(String body, String userPosted, String memeId) {
        this.body = body;
        this.userPosted = userPosted;
        this.memeId = memeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserPosted() {
        return userPosted;
    }

    public void setUserPosted(String userPosted) {
        this.userPosted = userPosted;
    }

    public String getMemeId() {
        return memeId;
    }

    public void setMemeId(String memeId) {
        this.memeId = memeId;
    }
}