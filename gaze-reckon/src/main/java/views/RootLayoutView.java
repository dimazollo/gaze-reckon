package views;

import controllers.DataController;
import dataRecovery.DataRecovery;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.Main;
import model.MappedDataItem;
import model.test.Stimulus;
import serializer.Serializer;
import statistics.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Dmitry Volovod
 * created on 17.10.2015
 */
public class RootLayoutView {
    private final Property<Main> mainApp = new SimpleObjectProperty<>();
    @FXML
    private MenuItem btnShowCharts;

    @FXML
    private CheckMenuItem linearInterpolationFlag;
    @FXML
    private CheckMenuItem listwiseDeletionFlag;
    @FXML
    private CheckMenuItem simpleRecoveryFlag;
    @FXML
    private CheckMenuItem smoothingFilterFlag;
    @FXML
    private RadioMenuItem commaFlag;
    @FXML
    private RadioMenuItem semicolonFlag;
    @FXML
    private ToggleGroup fieldSeparatorToggleGroup;
    @FXML
    private CheckMenuItem useConfigFlag;

    @FXML
    private void initialize() {
        linearInterpolationFlag.setSelected(true);
        simpleRecoveryFlag.setSelected(true);
        listwiseDeletionFlag.setSelected(false);
    }

    public void showCharts() {
        mainApp.getValue().getViewOfDataStage().show();
        if (mainApp.getValue().getStimuli() != null) {
            mainApp.getValue().getViewOfData().updateDistancesChart(); // Обновление графика с расстояниями перед открытием окошка с графиками.
        }
//        Platform.runLater(() -> mainApp.getValue().getViewOfData().initTraceChart());
        Platform.runLater(() -> mainApp.getValue().getViewOfData().initCoordinatesGraph());
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        System.out.println();
        alert.setHeaderText("Gaze Reckon");
        alert.setContentText("Application for processing data files\n" +
                "received from \"The Eye Tribe\" eye-tracker.\n\n" +
                "Dmitry Volovod, Mikhail Turicyn\n" +
                "INPE NRNU MEPHI, 2015");
        alert.showAndWait();
    }

    @FXML
    private void handleWriteData() {
        ParserView parserView = mainApp.getValue().getParserView();
        parserView.handleStartBtn();
    }

    @FXML
    private void handleComputeErrorsZones() {
        mainApp.getValue().setMessages(DataRecovery.listwiseDeletion(mainApp.getValue().getMessages()));
//        LinkedHashMap<Message, Stimulus> mappedData = DataController.mapTrackersAndStimuli(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
//        LinkedHashMap<Message, Stimulus> filteredMap = Error.filterStimulusMessageMap(mappedData);
        ArrayList<MappedDataItem> mappedDataList = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
//        HashMap<Stimulus, Double[]> deviations = Error.computeDeviationsForEach(filteredMap);
        HashMap<Stimulus, Double[]> stats = Error.computeDeviations(mappedDataList);
//        System.out.println("Stimulus x;Stimulus y;absolute deviation x;absolute deviation y;standard deviation x;standard deviation y");
        System.out.println("Stimulus x;Stimulus y;absolute deviation x;absolute deviation y;" +
                "standard deviation x;standard deviation y;" +
                "min abs deviation x;max abs deviation x;min abs deviation y; max abs deviation y;");
        for (Map.Entry<Stimulus, Double[]> entry : stats.entrySet()) {
            System.out.println(entry.getKey().getPosition().x + ";" + entry.getKey().getPosition().y + ";" +
                    entry.getValue()[0] + ";" + entry.getValue()[1] + ";" + entry.getValue()[2] + ";" + entry.getValue()[3] + ";" +
                    entry.getValue()[4] + ";" + entry.getValue()[5] + ";" + entry.getValue()[6] + ";" + entry.getValue()[7]);
        }
    }

    @FXML
    private void handleComputeErrors() {
        mainApp.getValue().setMessages(DataRecovery.listwiseDeletion(mainApp.getValue().getMessages()));
        ArrayList<MappedDataItem> mappedDataList = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
        Double[] results = Error.computeDeviationsForAll(mappedDataList);
        System.out.println("absDevX\tabsDevY\tstDevX\tstDevY\n" + results[0] + "\t" + results[1] + "\t" + results[2] + "\t" + results[3]);
    }

    @FXML
    private void handleNormalityTest() {
        mainApp.getValue().setMessages(DataRecovery.listwiseDeletion(mainApp.getValue().getMessages()));
        ArrayList<MappedDataItem> mappedDataList = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
        HashMap<Stimulus, Boolean[]> stats = Error.normalityTest(mappedDataList);
        for (Map.Entry<Stimulus, Boolean[]> entry : stats.entrySet()) {
            System.out.println(entry.getKey().getPosition().x + ";" + entry.getKey().getPosition().y + ";" +
                    entry.getValue()[0] + ";" + entry.getValue()[1]);
        }
//        HashMap<Stimulus, Boolean> stats = Error.normalityTestForDeviations(mappedDataList);
//        for (Map.Entry<Stimulus, Boolean> entry : stats.entrySet()) {
//            System.out.println(entry.getKey().getPosition().x + ";" + entry.getKey().getPosition().y + ";" +
//                    entry.getValue());
//        }
        int tr = 0, all = 0;
        for (Boolean[] b : stats.values()) {
            all += 2;
            for (int i = 0; i < b.length; i++) {
                if (b[i] != null) {
                    if (b[i]) tr++;
                }
            }
        }
        System.out.println("tr = " + tr + "\tall = " + all + "\tk = " + ((float) tr / (float) all));
    }

    @FXML
    private void handleSigelTukeyTest() {
        mainApp.getValue().setMessages(DataRecovery.listwiseDeletion(mainApp.getValue().getMessages()));
        ArrayList<MappedDataItem> mappedDataList = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
        HashMap<Stimulus[], Boolean> stats = Error.sigelTukeyTest(mappedDataList);
        System.out.println("Sigel-Tukey test\nStimulus 1 x;Stimulus 1 y;Stimulus 2 x;Stimulus 2 y;Sigel-Tukey test");
        double k = 0, all = 0;
        for (Map.Entry<Stimulus[], Boolean> entry : stats.entrySet()) {
            System.out.println("" + entry.getKey()[0] + entry.getKey()[1] + entry.getValue());
            if (entry.getValue()) k++;
            all++;
        }
        System.out.println("k  = " + k / all);
    }

    public void handleCountMissesByStimulus() {
        ArrayList<MappedDataItem> mappedDataItem = DataController.createMappedData(mainApp.getValue().getMessages(), mainApp.getValue().getStimuli());
        HashMap<Stimulus, Integer> stats = Error.countMissesByStimulus(mappedDataItem);
        System.out.println("Misses by stimulus statistic:");
        for (Map.Entry<Stimulus, Integer> entry : stats.entrySet()) {
            System.out.println("" + entry.getKey() + entry.getValue());
        }
    }

    @FXML
    private void handleExit() {
        Serializer deserializer = new Serializer(mainApp.getValue());
        deserializer.store();
        System.exit(0);
    }

    public String getFieldSeparator() {
        if (commaFlag.isSelected()) return ",";
        else if (semicolonFlag.isSelected()) return ";";
        else return null;
    }

    public Boolean getLinearInterpolationFlag() {
        return linearInterpolationFlag.isSelected();
    }

    public void setLinearInterpolationFlag(Boolean flag) {
        this.linearInterpolationFlag.setSelected(flag);
    }

    public Boolean getListwiseDeletionFlag() {
        return listwiseDeletionFlag.isSelected();
    }

    public void setListwiseDeletionFlag(Boolean flag) {
        this.listwiseDeletionFlag.setSelected(flag);
    }

    public Boolean getSimpleRecoveryFlag() {
        return simpleRecoveryFlag.isSelected();
    }

    public void setSimpleRecoveryFlag(Boolean flag) {
        this.simpleRecoveryFlag.setSelected(flag);
    }

    public Boolean getSmoothingFilter() {
        return smoothingFilterFlag.isSelected();
    }

    public Boolean getCommaFlag() {
        return commaFlag.isSelected();
    }

    public void setCommaFlag(Boolean flag) {
        this.commaFlag.setSelected(flag);
    }

    public Boolean getSemicolonFlag() {
        return semicolonFlag.isSelected();
    }

    public void setSemicolonFlag(Boolean flag) {
        this.semicolonFlag.setSelected(flag);
    }

    public Boolean getUseConfigFlag() {
        return useConfigFlag.isSelected();
    }

    public void setUseConfigFlag(Boolean flag) {
        useConfigFlag.setSelected(flag);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp.setValue(mainApp);
    }
}