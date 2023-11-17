package com.duokhongg.audiorecorder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class Helper {
    public static String formatDuration(String durationInMillis) {
        long duration = Long.parseLong(durationInMillis);
        long millis = duration % 1000;
        long seconds = (duration / 1000) % 60;
        if (millis >= 500) {
            seconds++;
        }
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));

        String formatted;
        if (hours > 0) {
            formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            formatted = String.format("%02d:%02d", minutes, seconds);
        }

        return formatted;
    }

    public static String formatLastModified(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateToCompareFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(timeStamp);

        String fullModifiedDate = dateFormat.format(date);
        String[] temp = fullModifiedDate.split(" ");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            try {
                LocalDate modifiedDate = LocalDate.parse(dateToCompareFormat.format(date));
                if (modifiedDate.equals(currentDate)) {
                    return "Hôm nay, " + temp[1];
                } else {
                    return fullModifiedDate;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}
