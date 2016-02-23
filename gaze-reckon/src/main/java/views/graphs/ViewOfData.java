package views.graphs;

import controllers.DataController;
import javafx.beans.property.Property;
import main.Main;
import model.eyetracker.Message;
import model.test.Stimulus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Vano on 22.02.2016.
 */
public class ViewOfData {
    Property<Main> mainApp;

    public void metod() {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        //messages.get(0).values.frame.avg
    }

    public void setMainApp(Main mainApp) {
        this.mainApp.setValue(mainApp);
    }

    // Вычисление расстояний от точки взгляда до центра мишени.
    // Например: computeDistances(Message.DELTA_AVERAGE, mappedData) вернёт только расстояния для полей "deltaAverage";
    private ArrayList<ArrayList<Double>> computeDistances(String column) {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        ArrayList<Stimulus> stimuli = mainApp.getValue().getStimuli();
        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(messages, stimuli);
        return DataController.computeDistances(column, mappedData);
    }
}
