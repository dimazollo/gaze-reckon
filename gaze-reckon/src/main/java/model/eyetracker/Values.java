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
public class Values {

    public Frame frame;

    public Values() {
        frame = new Frame();
    }

    public String toString() {
        return frame.toString();
    }
}
