/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.eyetracker;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Eye {
    public Point avg;
    public Point raw;
    public Point pcenter;
    double psize;

    public Eye() {
        avg = new Point();
        pcenter = new Point();
        psize = 0;
        raw = new Point();
    }

    public boolean hasMissingData() {
        return raw.x == 0.0 || raw.y == 0.0;
    }

    public String toString() {
        return "avg = " + avg.toString() + System.lineSeparator() +
                "Raw = " + raw.toString() + System.lineSeparator() +
                "pcenter = " + pcenter.toString() + System.lineSeparator() +
                "psize = " + psize;
    }
}
