package views;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.Main;
import serializer.Serializer;

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
        mainApp.getValue().getViewOfData().updateDistancesGraph(); // Обновление графика с расстояниями перед открытием окошка с графиками.
        mainApp.getValue().getViewOfDataStage().show();
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
