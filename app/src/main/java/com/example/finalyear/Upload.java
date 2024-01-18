package com.example.finalyear;

public class Upload {
    private String imageUrl;
    private String analysisResult;

    // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    public Upload() {
    }

    public Upload(String imageUrl, String analysisResult) {
        this.imageUrl = imageUrl;
        this.analysisResult = analysisResult;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }
}
