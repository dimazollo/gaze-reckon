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

    public static final DateFormat inputDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:S");
    public static final DateFormat outputDateFormat = new SimpleDateFormat("HH:mm:ss.S");

    private Date timestamp;
    // Поле timer есть во входном файле, но оно не используется внутри программы.
    // Т.е. удалять его нужно из программы предъявления стимулов, а потом уже здесь.
    private Double timer;
    private Point position;

    public Stimulus(String s) throws ParseException, IndexOutOfBoundsException {
        String[] strings = s.split("\t");
        timestamp = inputDateFormat.parse(strings[0]);
        timer = Double.parseDouble(strings[1]);
        position = new Point(Double.parseDouble(strings[2]), Double.parseDouble(strings[3]));
    }

    public Stimulus(double x, double y, Date timestamp) {
        this.position = new Point();
        this.position.x = x;
        this.position.y = y;
        this.timestamp = timestamp;
        this.timer = null;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Point getPosition() {
        return position;
    }

    public String getTimestampAsString() {
        String timestamp;
        try {
            timestamp = outputDateFormat.format(this.timestamp);
        } catch (NullPointerException e) {
            timestamp = "0";
        }
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
     /* obj ссылается на null */
        if (obj == null)
            return false;
     /* Удостоверимся, что ссылки имеют тот же самый тип */
        if (!(getClass() == obj.getClass()))
            return false;
        else {
            Stimulus tmp = (Stimulus) obj;
            if (tmp.getPosition().x == this.position.x && tmp.getPosition().y == this.position.y &&
                    tmp.getTimestamp() == this.timestamp)
                return true;
            else
                return false;
        }
    }

    public String toString() {
        return getTimestampAsString() + ";" + position.toString() + ";";
    }
}
