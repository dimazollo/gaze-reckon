/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.test;

import model.eyetracker.Point;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Stimulus {
    public static final String POSITION = "stimulus position";

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:S");
    private Date timestamp;
    private Double timer;
    private Point position;

    public Stimulus(String s) throws ParseException, IndexOutOfBoundsException {
        String[] strings = s.split("\t");
        timestamp = dateFormat.parse(strings[0]);
        timer = Double.parseDouble(strings[1]);
        position = new Point(Double.parseDouble(strings[2]), Double.parseDouble(strings[3]));
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Point getPosition() {
        return position;
    }

    public String getTimestampAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
        String timestamp;
        try {
            timestamp = dateFormat.format(this.timestamp);
        } catch (NullPointerException e) {
            timestamp = "0";
        }
        return timestamp;
    }

    public String toString() {
        return getTimestampAsString() + ";" + position.toString() + ";";
    }
}
