package views.graphs;

import controllers.DataController;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import main.Main;
import model.eyetracker.Message;
import model.test.Stimulus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Vano on 22.02.2016.
 */
public class ViewOfData {
    @FXML
    private Label lblCount;
    @FXML
    private Slider sliderCountStimuls;

    @FXML
    private TextField countStimuls;
    private Property<Main> mainApp;
    @FXML
    private NumberAxis botAxis;
    @FXML
    private NumberAxis leftAxis;
    @FXML
    private LineChart lineChart;

    private void viewing(int countStimuls) {
        lineChart.setTitle("Graphs");
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Distances between points and center of the stimulus");
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList(); //data - данные во множественном числе. datum - данные в единственном числе
        ArrayList<ArrayList<Double>> delta = computeDistances(Message.DELTA_AVERAGE);
        //ArrayList<ArrayList<Double>> time = mainApp.getValue().getStimuli().get(0).getTimestampAsString();
        if (countStimuls == 0) {
            countStimuls = delta.size();
        }
        if (countStimuls > delta.size()) {
            return;
        }
        int count = 0;
        for (int i = 0; i < countStimuls; i++) {
            for (int j = 0; j < delta.get(i).size(); j++) {
                data.add(new XYChart.Data(count, delta.get(i).get(j)));
                count++;
            }
        }
        series1.setData(data);
        lineChart.getData().add(series1);
    }


    public void setMainApp(Main mainApp) {
        this.mainApp = new SimpleObjectProperty<>();
        this.mainApp.setValue(mainApp);
    }

    // Производит вычисление расстояний от точки взгляда до центра мишени и возвращает по массиву таких расстояний для каждого стимула
    // Например: computeDistances(Message.DELTA_AVERAGE, mappedData) вернёт только расстояния по "deltaAverage";
    private ArrayList<ArrayList<Double>> computeDistances(String column) {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        ArrayList<Stimulus> stimuli = mainApp.getValue().getStimuli();
        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(messages, stimuli);
        return DataController.computeDistances(column, mappedData);
    }


    public void showStimuls(ActionEvent actionEvent) {
        ArrayList<ArrayList<Double>> delta = computeDistances(Message.DELTA_AVERAGE);
        lineChart.getData().clear();
//        Integer count = Integer.valueOf(countStimuls.getText());
        sliderCountStimuls.setMin(1);
        sliderCountStimuls.setMax(delta.size());
        int sliderCount = (int) sliderCountStimuls.getValue();
        viewing(sliderCount);
        updateCountLabel();
    }

   /* @FXML
    private void initialize() {
        initListener();

    }*/

    public void updateCountLabel() {
        lblCount.setText(String.valueOf((int)sliderCountStimuls.getValue()));
    }

   /* private void initListener() {//TODO надо исправить слушателя
        sliderCountStimuls.getProperties().addListener(->{
            updateCountLabel();
        });
    }*/
}
