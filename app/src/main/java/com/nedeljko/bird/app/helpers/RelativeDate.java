package com.nedeljko.bird.app.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RelativeDate {

    public static String getString(Date date) {
        Date now = new Date();
        long duration  = now.getTime()-date.getTime();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long weeks = TimeUnit.MILLISECONDS.toDays(duration)/7;
        long years = TimeUnit.MILLISECONDS.toDays(duration)/365;

        Formatter formatter = new Formatter(new StringBuilder(), Locale.getDefault());
        if (years > 0) {
            return formatter.format("%dy", years).toString();
        } else if (weeks > 0) {
            return formatter.format("%dw", weeks).toString();
        } else if (days > 0) {
            return formatter.format("%dd", days).toString();
        } else if (hours > 0) {
            return formatter.format("%dh", hours).toString();
        } else if (minutes > 0) {
            return formatter.format("%dm", minutes).toString();
        } else if (seconds > 0) {
            return formatter.format("%ds", seconds).toString();
        } else {
            return "Just now";
        }
    }
}
