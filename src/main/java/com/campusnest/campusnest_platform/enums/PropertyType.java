package com.campusnest.campusnest_platform.enums;

public enum PropertyType {
    DORM_ROOM("Dorm Room"),
    APARTMENT("Apartment"),
    HOUSE("House"),
    STUDIO("Studio"),
    ROOM_IN_APARTMENT("Room in Apartment"),
    ROOM_IN_HOUSE("Room in House"),
    TEMPORARY_STAY("Temporary Stay");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}