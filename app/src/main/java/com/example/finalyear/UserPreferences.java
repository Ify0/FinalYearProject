package com.example.finalyear;

public class UserPreferences {
    private String priceRange;
    private String priority;
    private boolean dry;
    private boolean normalSkin;
    private boolean oily;
    private boolean combination;

    // Constructors
    public UserPreferences() {
        // Default constructor required for Firebase
    }

    public UserPreferences(String priceRange, String priority, boolean dry, boolean normalSkin, boolean oily, boolean combination) {
        this.priceRange = priceRange;
        this.priority = priority;
        this.dry = dry;
        this.normalSkin = normalSkin;
        this.oily = oily;
        this.combination = combination;
    }


    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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
}

