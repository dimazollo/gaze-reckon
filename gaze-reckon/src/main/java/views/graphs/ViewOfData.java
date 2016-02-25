package views.graphs;

import controllers.DataController;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private ArrayList<ArrayList<Double>> distances;
    private Property<Main> mainApp;
    @FXML
    private Label lblCount;
    @FXML
    private Slider sliderCountStimuli;
    @FXML
    private TextField countStimuli;
    @FXML
    private NumberAxis botAxis;
    @FXML
    private NumberAxis leftAxis;
    @FXML
    private LineChart lineChart;


    @FXML
    private void initialize() {
        initListeners();
    }


    public void updateDistancesGraph() {
        //TODO - Это условие и подсчёт расстояний нужно отсюда безопасно убрать.
        if(distances==null){
            distances = computeDistances(Message.DELTA_AVERAGE);
        }
        lineChart.getData().clear();
        sliderCountStimuli.setMin(1);
        sliderCountStimuli.setMax(distances.size());
        int sliderValue = (int) sliderCountStimuli.getValue();
        plotLineChart(sliderValue);
    }


    private void plotLineChart(int indexOfStimuli) {
        lineChart.setTitle("Graphs");
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Distances between points and center of the stimulus");
        //ArrayList<ArrayList<Double>> time = mainApp.getValue().getStimuli().get(0).getTimestampAsString();
        ObservableList<XYChart.Data> distancesLineChartData = FXCollections.observableArrayList();
        if (indexOfStimuli == 0) {
            indexOfStimuli = distances.size();
        }
        if (indexOfStimuli > distances.size()) {
            return;
        }
        int count = 0;
        //TODO - Следует ограничить максимальное количество точек на графике. Если много точек - выводить некие средние или только каждую k-ю (каждую 5-ю, например).
        for (int i = indexOfStimuli - 1; i < indexOfStimuli; i++) {  //В таком виде выводит только 1 стимул на график единовременно. (i = 0 чтобы выводить с начала)
            for (int j = 0; j < distances.get(i).size(); j++) {
                distancesLineChartData.add(new XYChart.Data(count, distances.get(i).get(j)));
                count++;
            }
        }
        series1.setData(distancesLineChartData);
        lineChart.getData().add(series1);
    }


    // Производит вычисление расстояний от точки взгляда до центра мишени и возвращает по массиву таких расстояний для каждого стимула
    // Например: computeDistances(Message.DELTA_AVERAGE, mappedData) вернёт только расстояния по "deltaAverage";
    private ArrayList<ArrayList<Double>> computeDistances(String column) {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        ArrayList<Stimulus> stimuli = mainApp.getValue().getStimuli();
        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(messages, stimuli);
        return DataController.computeDistances(column, mappedData);
    }


    private void initListeners() {
        sliderCountStimuli.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblCount.setText(String.valueOf(newValue.intValue()));
            // Обновление графика при изменении положения слайдера (ваш Дмитрий).
            updateDistancesGraph();
        });
    }


    public void setMainApp(Main mainApp) {
        this.mainApp = new SimpleObjectProperty<>();
        this.mainApp.setValue(mainApp);
    }
}
