package com.campusnest.campusnest_platform.enums;

public enum ListingStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    COMPLETED("Completed"),
    EXPIRED("Expired");

    private final String displayName;

    ListingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}