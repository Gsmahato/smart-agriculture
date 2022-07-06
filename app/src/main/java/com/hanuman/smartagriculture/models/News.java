package com.hanuman.smartagriculture.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class News implements Serializable {
    @Exclude
    private String key;

    String news_id, news_title,news_summary, news_source, news_author, news_image, news_link,news_date;

    public News(String news_id,String news_title, String news_summary, String news_source, String news_author, String news_image, String news_link, String news_date) {
        this.news_id = news_id;
        this.news_title = news_title;
        this.news_summary = news_summary;
        this.news_source = news_source;
        this.news_author = news_author;
        this.news_image = news_image;
        this.news_link = news_link;
        this.news_date = news_date;
    }
    public News(String news_title, String news_summary, String news_source,String news_link,String news_date) {
        this.news_title = news_title;
        this.news_summary = news_summary;
        this.news_source = news_source;
        this.news_link=news_link;
        this.news_date = news_date;
    }
    public News(){}

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_summary() {
        return news_summary;
    }

    public void setNews_summary(String news_summary) {
        this.news_summary = news_summary;
    }

    public String getNews_source() {
        return news_source;
    }

    public void setNews_source(String news_source) {
        this.news_source = news_source;
    }

    public String getNews_author() {
        return news_author;
    }

    public void setNews_author(String news_author) {
        this.news_author = news_author;
    }

    public String getNews_image() {
        return news_image;
    }

    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }

    public String getNews_link() {
        return news_link;
    }

    public void setNews_link(String news_link) {
        this.news_link = news_link;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
