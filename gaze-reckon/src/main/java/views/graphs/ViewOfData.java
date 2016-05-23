package views.graphs;

import controllers.DataController;
import dataRecovery.Regression;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import main.Main;
import model.MappedDataItem;
import model.eyetracker.Message;
import model.test.Stimulus;
import org.apache.commons.math3.util.Pair;
import org.controlsfx.control.RangeSlider;
import simplify.Simplify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Ivan, Dmitry
 *         created on 22.02.2016.
 */
public class ViewOfData {

    private final int RANGE_SLIDER_MAJOR_TICK_NUMBER = 5;
    private final int X_AXIS_SEGMENTS = 7;

    private final DateFormat sliderDateFormat = new SimpleDateFormat("H:mm");
    private final DateFormat labelDateFormat = new SimpleDateFormat("H:mm:ss.S");
    private final DateFormat axisDateFormat = new SimpleDateFormat("H:mm:ss.S");

    private ArrayList<ArrayList<Double>> distances;
    private Property<Main> mainApp;

    @FXML
    private Label lblCount;
    @FXML
    private Slider countStimuliSlider;
    @FXML
    private LineChart<Integer, Double> distancesChart;
    @FXML
    private NumberAxis xDistancesAxis;
    @FXML
    private NumberAxis yDistancesAxis;
    @FXML
    private ScatterChart<Double, Double> traceChart;
    @FXML
    private NumberAxis xTraceAxis;
    @FXML
    private NumberAxis yTraceAxis;
    @FXML
    private LineChart<Long, Double> coordinatesChart;
    @FXML
    private NumberAxis xCoordinatesAxis;
    @FXML
    private NumberAxis yCoordinatesAxis;
    @FXML
    private RangeSlider coordinatesRangeSlider;
    @FXML
    private Label lowBoundLabel;
    @FXML
    private Label highBoundLabel;
    @FXML
    private TextField widthOfSmoothingWindow;
    @FXML
    private TextField toleranceTextField;
    @FXML
    private Label numberOfPointsLabel;

    @FXML
    private void initialize() {
//        initTraceChart();
//        initCoordinatesGraph();
        initListeners();
    }

    public void updateDistancesChart() {
        //TODO - Это условие и подсчёт расстояний нужно отсюда безопасно убрать.
        if (distances == null) {
            distances = computeDistances(Message.DELTA_RAW);
        }
        distancesChart.getData().clear();
        countStimuliSlider.setMin(0);
        countStimuliSlider.setMax(distances.size() - 1);
        int sliderValue = (int) countStimuliSlider.getValue();
        plotDistancesChart(sliderValue);
    }

    private void plotDistancesChart(int indexOfStimulus) {
//        distancesChart.setTitle("Distances between points and center of the stimulus");
//        ArrayList<ArrayList<Double>> time = mainApp.getValue().getStimuli().get(0).getTimestampAsString();
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();

        if (indexOfStimulus == 0) indexOfStimulus = distances.size();
        if (indexOfStimulus > distances.size()) return;

        int count = 0;
        //TODO - Следует ограничить максимальное количество точек на графике. Использовать алгоритм Рамера-Дугласа-Пекера.
        for (int i = indexOfStimulus - 1; i < indexOfStimulus; i++) {  // В таком виде выводит только 1 стимул на график единовременно. (i = 0 чтобы выводить с начала) (indexOfStimulus - 1)
            for (int j = 0; j < distances.get(i).size(); j++) {
                series.getData().add(new XYChart.Data<>(count, distances.get(i).get(j)));
                count++;
            }
        }
        distancesChart.getData().add(series);
    }

    public void initTraceChart() {
        // Убираем минуса с отрицательной части вертикальной оси.
        yTraceAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(-1 * object.intValue());
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        });

        ArrayList<MappedDataItem> mappedData = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
        traceChart.getData().clear();
        LineChart.Series<Double, Double> traceSeries = new LineChart.Series<>();
        LineChart.Series<Double, Double> stimuliSeries = new LineChart.Series<>();
        // Should do it with Animation class.
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                traceSeries.setName("Gaze movements trace");
                stimuliSeries.setName("Stimuli positions");
                Platform.runLater(() -> {
                    traceChart.getData().add(traceSeries);
                    traceChart.getData().add(stimuliSeries);
                });

                for (int i = 0; i < mappedData.size(); i++) {
                    final int finalI = i;

                    Platform.runLater(() -> {
                        stimuliSeries.getData().add(new LineChart.Data<>(
                                mappedData.get(finalI).getStimulus().getPosition().x,
                                -mappedData.get(finalI).getStimulus().getPosition().y));
                        if (stimuliSeries.getData().size() > 3) {
                            stimuliSeries.getData().remove(0);
                            while (traceSeries.getData().size() > 90 * 1.9) {
                                traceSeries.getData().remove(0);
                            }
                        }
                    });

                    for (int j = 0; j < mappedData.get(i).getMessages().size(); j++) {
                        final int finalJ = j;
                        Platform.runLater(() -> traceSeries.getData().add(new LineChart.Data<>(
                                mappedData.get(finalI).getMessages().get(finalJ).values.frame.avg.x,
                                -mappedData.get(finalI).getMessages().get(finalJ).values.frame.avg.y)));
                        Thread.sleep(15);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void initCoordinatesGraph() {
        // Setting of visual parameters.
        long minValue = mainApp.getValue().getMessages().get(0).values.frame.timestamp.getTime();
        long maxValue = mainApp.getValue().getMessages().get(mainApp.getValue().getMessages().size() - 1).values.frame.timestamp.getTime();

        coordinatesRangeSlider.setMin(minValue);
        coordinatesRangeSlider.setMax(maxValue);
        coordinatesRangeSlider.setMajorTickUnit((maxValue - minValue) / RANGE_SLIDER_MAJOR_TICK_NUMBER);
        coordinatesRangeSlider.setHighValue(maxValue - (maxValue - minValue) / 4);
        coordinatesRangeSlider.setLowValue(minValue + (maxValue - minValue) / 4);
        coordinatesRangeSlider.setMinorTickCount(3);
        coordinatesRangeSlider.setLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return sliderDateFormat.format(object.longValue());
            }

            @Override
            public Number fromString(String string) {
                return Double.valueOf(string).longValue();
            }
        });

        xCoordinatesAxis.upperBoundProperty().bind(coordinatesRangeSlider.highValueProperty());
        xCoordinatesAxis.lowerBoundProperty().bind(coordinatesRangeSlider.lowValueProperty());
        xCoordinatesAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return axisDateFormat.format(object.longValue());
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        // Creating series and adding them to the chart.
        List<Pair<Long, Double>> xPoints = preparePlottingData(mainApp.getValue().getMessages(), Field.AVG_X);
        xPoints = Simplify.simplify(xPoints, Double.parseDouble(toleranceTextField.getText()), true);
//        List<Pair<Long, Double>> yPoints = preparePlottingData(mainApp.getValue().getMessages(), Field.AVG_Y);
        List<Pair<Long, Double>> smoothedXPoints = Regression.nadarayaWatson(xPoints, Double.parseDouble(widthOfSmoothingWindow.getText()), true);
//        addSeriesToChart(xPoints, "X", coordinatesChart);
//        addSeriesToChart(yPoints, coordinatesChart);
        addSeriesToChart(smoothedXPoints, "Smoothed X", coordinatesChart);
    }

    private void addSeriesToChart(List<Pair<Long, Double>> points, String seriesName, XYChart chart) {
        for (int i = 0; i < coordinatesChart.getData().size(); i++) {
            if (coordinatesChart.getData().get(i).getName().equals(seriesName)) {
                coordinatesChart.getData().remove(i);
            }
        }
        LineChart.Series<Long, Double> series = new XYChart.Series<>();
        series.setName(seriesName);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < points.size(); i += 1) {
                    //noinspection SuspiciousNameCombination
                    series.getData().add(new XYChart.Data<>(points.get(i).getKey(), points.get(i).getValue()));
                }
//                points.forEach(p -> series.getData().add(new XYChart.Data<>(p.getKey(), p.getValue())));
                Platform.runLater(() -> chart.getData().add(series));
                return null;
            }
        };
        new Thread(task).start();
    }

    // Производит вычисление расстояний от точки взгляда до центра мишени и возвращает по массиву таких расстояний для каждого стимула
    // Например: computeDistances(Message.DELTA_AVERAGE, mappedData) вернёт только расстояния по "deltaAverage";
    private ArrayList<ArrayList<Double>> computeDistances(String column) {
        ArrayList<Message> messages = mainApp.getValue().getMessages();
        ArrayList<Stimulus> stimuli = mainApp.getValue().getStimuli();
        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(messages, stimuli);
        return DataController.computeDistances(column, mappedData);
    }

    public static List<Pair<Long, Double>> preparePlottingData(ArrayList<Message> messages, Field field) {
        List<Pair<Long, Double>> preparedList = new ArrayList<>();
        switch (field) {
            case RAW_X:
                messages.forEach(m -> preparedList.add(new Pair<>(m.values.frame.timestamp.getTime(), m.values.frame.raw.x)));
                break;
            case RAW_Y:
                messages.forEach(m -> preparedList.add(new Pair<>(m.values.frame.timestamp.getTime(), m.values.frame.raw.y)));
                break;
            case AVG_X:
                messages.forEach(m -> preparedList.add(new Pair<>(m.values.frame.timestamp.getTime(), m.values.frame.avg.x)));
                break;
            case AVG_Y:
                messages.forEach(m -> preparedList.add(new Pair<>(m.values.frame.timestamp.getTime(), m.values.frame.avg.y)));
        }
        return preparedList;
    }

    private void initListeners() {
        countStimuliSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateDistancesChart();
        });
        countStimuliSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblCount.setText(String.valueOf(observable.getValue().intValue()));
            // Постоянное Обновление графика при изменении положения слайдера.
//            if (!countStimuliSlider.isValueChanging()) updateDistancesChart();
        });
        // Binding range slider's low value to the low bound label.
        coordinatesRangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            lowBoundLabel.setText(labelDateFormat.format(newValue.longValue()));
            xCoordinatesAxis.setTickUnit((coordinatesRangeSlider.getHighValue() - coordinatesRangeSlider.getLowValue()) / X_AXIS_SEGMENTS);
        });
        // Binding range slider's high value to the high bound label.
        coordinatesRangeSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            highBoundLabel.setText(labelDateFormat.format(newValue.longValue()));
            xCoordinatesAxis.setTickUnit((coordinatesRangeSlider.getHighValue() - coordinatesRangeSlider.getLowValue()) / X_AXIS_SEGMENTS);
        });
    }


    public void setMainApp(Main mainApp) {
        this.mainApp = new SimpleObjectProperty<>();
        this.mainApp.setValue(mainApp);
    }

    @FXML
    private void updateSmoothingWindow(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            event.consume();
            List<Pair<Long, Double>> xPoints = preparePlottingData(mainApp.getValue().getMessages(), Field.AVG_X);
            xPoints = Simplify.simplify(xPoints, Double.parseDouble(toleranceTextField.getText()), true);
            xPoints = Regression.nadarayaWatson(xPoints, Double.parseDouble(widthOfSmoothingWindow.getText()), true);

            addSeriesToChart(xPoints, "Smoothed X", coordinatesChart);
            numberOfPointsLabel.setText(String.valueOf(xPoints.size()));
        }
    }

    public enum Field {
        AVG_X, AVG_Y, RAW_X, RAW_Y
    }
}
