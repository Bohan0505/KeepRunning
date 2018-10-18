package comp5216.sydney.edu.au.keeprunning.util;

import android.database.Cursor;

import comp5216.sydney.edu.au.keeprunning.ProjectApp;

/**
 * Created by CharlesLui on 02/10/2017.
 */

public class Utils {

    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static double getTotalDistance(long startTime, long endTime) {
        Cursor cursor = ProjectApp.getInstance().getDb().getDatabase()
                .rawQuery("SELECT SUM(distance) AS \"Total Distance\" FROM RouteRecord WHERE datetime >= ? and datetime <= ?",
                        new String[]{String.valueOf(startTime), String.valueOf(endTime)});

        double totalDistanse = 0;
        while (cursor.moveToNext()) {
            totalDistanse += cursor.getDouble(cursor.getColumnIndex("Total Distance"));
        }
        cursor.close();

        return totalDistanse;
    }
}
