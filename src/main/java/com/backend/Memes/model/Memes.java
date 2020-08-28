package com.backend.Memes.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.List;

@Document(collection = "Memes")
public class Memes {

    @Id
    private String id;

    private String url;
    private List<String> hashTags = new ArrayList<String>();
    private int disLikes;
    private int likes;
    private boolean isTrending;

    @DBRef
    private List<Comments> comments = new ArrayList<Comments>();

    public Memes(String url, List<String> hashTags, int disLikes, int likes, boolean isTrending) {
        this.url = url;
        this.hashTags = hashTags;
        this.disLikes = disLikes;
        this.likes = likes;
        this.isTrending = isTrending;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public int getDisLikes() {
        return disLikes;
    }

    public void setDisLikes(int disLikes) {
        this.disLikes = disLikes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isTrending() {
        return isTrending;
    }

    public void setTrending(boolean trending) {
        isTrending = trending;
    }
}