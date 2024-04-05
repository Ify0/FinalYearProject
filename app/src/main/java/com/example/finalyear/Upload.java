package com.example.finalyear;

import com.google.firebase.Timestamp;

public class Upload {
    private String imageUrl;
    private String analysisResult;

    private Timestamp timestamp;
    // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    public Upload() {
    }

    public Upload(String imageUrl, String analysisResult, Timestamp timestamp) {
        this.imageUrl = imageUrl;
        this.analysisResult = analysisResult;
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
