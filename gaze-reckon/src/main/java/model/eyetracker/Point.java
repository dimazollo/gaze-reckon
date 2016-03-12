/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.eyetracker;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Point {

    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        x = 0;
        y = 0;
    }

    public Double getDistance(Point p) {
        return sqrt(pow(this.x - p.x, 2.0) + pow(this.y - p.y, 2.0));
    }

    public String getDescription(String fieldSeparator) {
        return "" + x + fieldSeparator + y;
    }

    @Override
    public String toString() {
        return "" + x + ";" + y;
    }
}
