package de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;

import java.util.Calendar;

/**
 * Used to define how long a {@link PermissionEntry}
 * will last until it expires and will be removed
 * from the given {@link ICloudPlayer}
 */
public enum PermissionValidity {

    /**
     * Expires after second(s)
     */
    SECOND,

    /**
     * Expires after minute(s)
     */
    MINUTE,

    /**
     * Expires after hour(s)
     */
    HOUR,

    /**
     * Expires after day(s)
     */
    DAY,

    /**
     * Expires after week(s)
     */
    WEEK,

    /**
     * Expires after month(s)
     */
    MONTH,

    /**
     * Expires after year(s)
     */
    YEAR,

    /**
     * Never expires and lasts forever
     */
    LIFETIME;


    /**
     * Transforms this validity to an int
     *
     * @return formed int
     */
    public int toCalendar() {
        if (this == PermissionValidity.SECOND) {
            return Calendar.SECOND;
        } else if (this == PermissionValidity.MINUTE) {
            return Calendar.MINUTE;
        } else if (this == PermissionValidity.HOUR) {
            return Calendar.HOUR;
        } else if (this == PermissionValidity.DAY) {
            return Calendar.DAY_OF_MONTH;
        } else if (this == PermissionValidity.WEEK) {
            return Calendar.WEEK_OF_MONTH;
        } else if (this == PermissionValidity.MONTH) {
            return Calendar.MONTH;
        } else if (this == PermissionValidity.YEAR || this == LIFETIME) {
            return Calendar.YEAR;
        } else {
            return Calendar.YEAR;
        }
    }

    /**
     * Formats the Validity from a String input
     * 
     * @param data the string input
     * @return validity
     */
    public static PermissionValidity formatValidity(String data) {
        PermissionValidity validity;
        if (data.equalsIgnoreCase("lifetime")) {
            validity = PermissionValidity.LIFETIME;
        } else {
            if (data.toLowerCase().endsWith("s")) {
                validity = PermissionValidity.SECOND;
            } else if (data.toLowerCase().endsWith("min")) {
                validity = PermissionValidity.MINUTE;
            } else if (data.toLowerCase().endsWith("h")) {
                validity = PermissionValidity.HOUR;
            } else if (data.toLowerCase().endsWith("d")) {
                validity = PermissionValidity.DAY;
            } else if (data.toLowerCase().endsWith("w")) {
                validity = PermissionValidity.WEEK;
            } else if (data.toLowerCase().endsWith("m")) {
                validity = PermissionValidity.MONTH;
            } else {
                validity = null;
            }
        }
        return validity;
    }
}
