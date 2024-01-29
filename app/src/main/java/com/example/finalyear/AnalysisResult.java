package com.example.finalyear;

public class AnalysisResult {
    private String predictedClass;
    private double confidence;

    // Constructors
    public AnalysisResult() {
        // Default constructor required for Firebase
    }

    public AnalysisResult(String predictedClass, double confidence) {
        this.predictedClass = predictedClass;
        this.confidence = confidence;
    }

    // Add getters and setters
    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = predictedClass;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

