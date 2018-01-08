package com.drivecom.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class VoiceMessageModel {
    public String filename;
    public String from;
    public List<String> to;
    public String url;

    public VoiceMessageModel() {
    }

    public VoiceMessageModel(String from, List<String> to, String filename, String url) {
        this.filename = filename;
        this.from = from;
        this.to = to;
        this.url = url;
    }
}
