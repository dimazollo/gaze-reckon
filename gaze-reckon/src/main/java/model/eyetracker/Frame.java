/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.eyetracker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Frame {
    public static final String RIGHT = "RIGHT";
    public static final String LEFT = "LEFT";
    public static final String BOTH = "BOTH";

    public Date timestamp;
    public boolean fix;
    public Point avg;
    public Point raw;
    public Eye lefteye;
    public Eye righteye;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        String timestamp;
        if (this.timestamp != null) {
            timestamp = dateFormat.format(this.timestamp);
        } else {
            timestamp = "0";
        }
        return timestamp;
    }

    public String hasMissingData() {
        if (righteye.hasMissingData() && lefteye.hasMissingData()) {
            return BOTH;       //both eyes position is not defined;
        } else if (righteye.hasMissingData()) {
            return RIGHT;       //right eye position is not defined;
        } else if (lefteye.hasMissingData()) {
            return LEFT;       //left eye position is not defined;
        } else return null; //both eyes data is correct;
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
