package views.graphs;

import controllers.DataController;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import main.Main;
import model.eyetracker.Message;
import model.test.Stimulus;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Created by Vano on 22.02.2016.
 */
public class ViewOfData implements Initializable {
    Property<Main> mainApp;
    @FXML
    private NumberAxis botAxis;
    @FXML
    private NumberAxis leftAxis;
    @FXML
    private LineChart lineChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewing();

    }

    private void viewing() {
        lineChart.setTitle("Graphs");
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Distances between points and center of the stimulus");
        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
        ArrayList<ArrayList<Double>> delta = computeDistances(Message.DELTA_AVERAGE);
        ArrayList<ArrayList<Double>> time = computeDistances(Message.TIMESTAMP);
        for (int i = 0; i < delta.size(); i++) {
            for (int j = 0; j < delta.get(i).size(); j++) {
                datas.add(new XYChart.Data(time.get(i).get(j), delta.get(i).get(j)));
            }
        }
        series1.setData(datas);
        lineChart.getData().add(series1);
    }


    public void setMainApp(Main mainApp) {
        this.mainApp.setValue(mainApp);
    }

    // Например: computeDistances(Message.DELTA_AVERAGE, mappedData) вернёт только значения полей "deltaAverage";
    private ArrayList<ArrayList<Double>> computeDistances(String column) {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        ArrayList<Stimulus> stimuli = mainApp.getValue().getStimuli();
        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(messages, stimuli);
        return DataController.computeDistances(column, mappedData);
    }
}
