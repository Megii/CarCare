package com.drivecom.utils;


public class InternalMessageModel {

    private String url;
    private String from;

    public InternalMessageModel(String url, String from){
        this.url = url;
        this.from = from;
    }

    public String getUrl() {
        return url;
    }

    public String getFrom() {
        return from;
    }
}
