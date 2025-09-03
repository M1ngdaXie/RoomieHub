package com.campusnest.campusnest_platform.enums;

public enum ListingType {
    OFFER("Offering Housing"),
    REQUEST("Looking for Housing");

    private final String displayName;

    ListingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}