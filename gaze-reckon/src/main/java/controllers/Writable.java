package controllers;

import javafx.scene.control.CheckBox;
import model.eyetracker.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @Author Dmitry Volovod
 * created on 10.11.2015
 */

//this interface is responsible for output results obtained while doing computations into the file
interface Writable {

    void write(LinkedHashMap<String, CheckBox> firedFlags, ArrayList<Message> messages, File csvFile) throws IOException;
}
