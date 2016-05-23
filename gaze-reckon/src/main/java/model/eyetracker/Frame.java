/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.eyetracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Frame {

    public static final DateFormat outputDateFormat = new SimpleDateFormat("HH:mm:ss.S");

    public static final String RIGHT = "RIGHT";
    public static final String LEFT = "LEFT";
    public static final String BOTH = "BOTH";

    final public Date timestamp;
    public boolean fix;
    final public Point avg;
    final public Point raw;
    final public Eye lefteye;
    final public Eye righteye;
    public int state;
    public long time;


    public Frame() {
        timestamp = null;
        fix = false;
        avg = new Point();
        raw = new Point();
        lefteye = new Eye();
        righteye = new Eye();
        state = 0;
        time = 0;
    }

    public String getTimestampAsString() {
        String timestamp;
        if (this.timestamp != null) {
            timestamp = outputDateFormat.format(this.timestamp);
        } else {
            timestamp = "0";
        }
        return timestamp;
    }

    public String hasMissingData() {
        if (righteye.hasMissingData() && lefteye.hasMissingData()) {
            return BOTH;        // Both eyes position is not defined.
        } else if (righteye.hasMissingData()) {
            return RIGHT;       // Right eye position is not defined.
        } else if (lefteye.hasMissingData()) {
            return LEFT;        // Left eye position is not defined.
        } else return null;     // All data is correct.
    }

    public String toString() {
        return "timeStamp = " + getTimestampAsString() + System.lineSeparator() +
                "avg = " + avg.toString() + System.lineSeparator() +
                "raw = " + raw.toString() + System.lineSeparator() +
                "leftEye (" + lefteye.toString() + ")" + System.lineSeparator() +
                "rightEye (" + righteye.toString() + ")" + System.lineSeparator() +
                "state = " + state + System.lineSeparator() +
                "time = " + time + System.lineSeparator();
    }
}
