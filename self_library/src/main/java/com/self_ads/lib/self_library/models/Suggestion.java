package com.self_ads.lib.self_library.models;

/**
 * Created by yassine on 22/11/2017.
 */

public class Suggestion {
    private int id;
    private String title;
    private String image;
    private String link;
    private Boolean myPackage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getMyPackage() {
        return myPackage;
    }

    public void setMyPackage(Boolean myPackage) {
        this.myPackage = myPackage;
    }
}
