package com.example.finalyear;

public class Report {

    private String reportID;
    private String PriceRange;
    private String Priority;
    private int range100;
    private boolean combination;
    private boolean darkCircles;
    private boolean dry;
    private boolean enlargedPores;
    private boolean fineLines;

    private boolean wrinkles;
    private boolean hyperpigmentation;
    private boolean normalSkin;
    private boolean oily;
    private boolean pimples;


    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }


    public String getPriceRange() {
        return PriceRange;
    }

    public void setPriceRange(String PriceRange) {
        this.PriceRange = PriceRange;
    }


    public String getPriority() {
        return Priority;
    }

    public void setPriority(String Priority) {
        this.Priority = Priority;
    }


    public int getRange100() {
        return range100;
    }

    public void setRange100(int range100) {
        this.range100 = range100;
    }


    public boolean isCombination() {
        return combination;
    }

    public void setCombination(boolean combination) {
        this.combination = combination;
    }


    public boolean isDarkCircles() {
        return darkCircles;
    }

    public void setDarkCircles(boolean darkCircles) {
        this.darkCircles = darkCircles;
    }


    public boolean isDry() {
        return dry;
    }

    public void setDry(boolean dry) {
        this.dry = dry;
    }


    public boolean isEnlargedPores() {
        return enlargedPores;
    }

    public void setEnlargedPores(boolean enlargedPores) {
        this.enlargedPores = enlargedPores;
    }

    public boolean isFineLines() {
        return fineLines;
    }

    public void setFineLines(boolean fineLines) {
        this.fineLines = fineLines;
    }


    public boolean isHyperpigmentation() {
        return hyperpigmentation;
    }

    public void setHyperpigmentation(boolean hyperpigmentation) {
        this.hyperpigmentation = hyperpigmentation;
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


    public boolean isPimples() {
        return pimples;
    }

    public void setPimples(boolean pimples) {
        this.pimples = pimples;
    }
}
