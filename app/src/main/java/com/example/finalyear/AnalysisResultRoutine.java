package com.example.finalyear;

public class AnalysisResultRoutine {
    private String priceRange;
    private String predictedClass;
    private boolean dry;
    private boolean normalSkin;
    private boolean oily;
    private boolean combination;

    // New fields
    private String imageUrl;
    private String analysisResult;

    public AnalysisResultRoutine() {
        // Default constructor required for Firebase
    }

    public AnalysisResultRoutine(String predictedClass, UserPreferences userPreferences) {
        this.predictedClass = predictedClass;
        this.priceRange = userPreferences.getPriceRange();
        this.dry = userPreferences.isDry();
        this.normalSkin = userPreferences.isNormalSkin();
        this.oily = userPreferences.isOily();
        this.combination = userPreferences.isCombination();
    }

    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = predictedClass;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public boolean isDry() {
        return dry;
    }

    public void setDry(boolean dry) {
        this.dry = dry;
    }

    public boolean isNormalSkin() {
        return normalSkin;
    }

    public void setNormalSkin(boolean normalSkin) {
        this.normalSkin = normalSkin;
    }

    public boolean isOily() {
        return oily;
    }

    public void setOily(boolean oily) {
        this.oily = oily;
    }

    public boolean isCombination() {
        return combination;
    }

    public void setCombination(boolean combination) {
        this.combination = combination;
    }

    // New getters and setters for imageUrl and analysisResult
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
}
