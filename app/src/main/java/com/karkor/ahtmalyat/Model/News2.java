package com.karkor.ahtmalyat.Model;


import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;

public class News2 {

    private String  titleNews;
    private String imageNews;
    private int vNumbers;
    private Date dateOfPost;
    private String videoNews;
    private String audioUrl;
    private ArrayList<String> listImagesUrl;
    private ArrayList<String> listVideosUrl;

    private int viewsNum;

    @Exclude
    private String idNews;

    public News2(){};

    public News2(String titleNews, String imageNews, Date dateOfPost, String videoNews){
        this.titleNews=titleNews;
        this.imageNews=imageNews;
        this.dateOfPost=dateOfPost;
        this.videoNews=videoNews;

    }
    public String getTitleNews() {
        return titleNews;
    }

    public void setTitleNews(String titleNews) {
        this.titleNews = titleNews;
    }

    public String getImageNews() {
        return imageNews;
    }

    public int getVNumbers() {
        return vNumbers;
    }

    public void setImageNews(String imageNews) {
        this.imageNews = imageNews;
    }

    public Date getDateOfPost() {
        return dateOfPost;
    }

    public void setDateOfPost(Date dateOfPost) {
        this.dateOfPost = dateOfPost;
    }



    public String getVideoNews() {
        return videoNews;
    }

    public void setVideoNews(String videoNews) {
        this.videoNews = videoNews;
    }

    @Exclude
    public String getIdNews() {
        return idNews;
    }
    @Exclude
    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    public int getViewsNum() {
        return viewsNum;
    }

    public void setViewsNum(int viewsNum) {
        this.viewsNum = viewsNum;
    }

    public ArrayList<String> getListImagesUrl() {
        return listImagesUrl;
    }

    public void setListImagesUrl(ArrayList<String> listImagesUrl) {
        this.listImagesUrl = listImagesUrl;
    }

    public ArrayList<String> getListVideosUrl() {
        return listVideosUrl;
    }

    public void setListVideosUrl(ArrayList<String> listVideosUrl) {
        this.listVideosUrl = listVideosUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
