package views;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Dmitry Volovod
 * created on 25.10.2015
 */
public class FDView {
    public static final String IDT = "IDT";
    public static final String HYPERBOLA = "Hyperbola";
    public static final String LNX = "Lnx";

    @FXML
    CheckBox idt;
    @FXML
    Label labelIdtLatency;
    @FXML
    TextField idtLatency;
    @FXML
    Label labelIdtThreshold;
    @FXML
    TextField idtThreshold;
    @FXML
    CheckBox hyperbola;
    @FXML
    CheckBox logarithm;

    @FXML
    private void initialize() {
        idt.selectedProperty().addListener((observable, oldValue, newValue) -> {
            labelIdtLatency.setDisable(oldValue);
            idtLatency.setDisable(oldValue);
            labelIdtThreshold.setDisable(oldValue);
            idtThreshold.setDisable(oldValue);
        });
        idt.fire();
        idt.fire(); //make the listener to give gui items right initial state;
    }

    public Integer getIdtLatencyAsInt() {
        Integer latency = null;
        try {
            Double t = Double.parseDouble(idtLatency.getText());
            latency = t.intValue();
        } catch (Exception ex) {
            showIncorrectContentAlert("Latency", ex);
        }
        return latency;
    }

    public Integer getIdtThresholdAsInt() {
        Integer threshold = null;
        try {
            Double t = Double.parseDouble(idtThreshold.getText());
            threshold = t.intValue();
        } catch (Exception ex) {
            showIncorrectContentAlert("Threshold", ex);
        }
        return threshold;
    }

    private void showIncorrectContentAlert(String s, Exception ex) {
        System.err.format("Exception: %s%n", ex);
        Logger.getLogger(FDView.class.getName()).log(Level.SEVERE, null, ex);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid parameter");
        alert.setContentText(s + " field contains incorrect parameter.");
        alert.showAndWait();
    }

    public CheckBox getIdt() {
        return idt;
    }

    public CheckBox getHyperbola() {
        return hyperbola;
    }

    public CheckBox getLogarithm() {
        return logarithm;
    }

    public String getIdtThreshold() {
        return idtThreshold.getText();
    }

    public void setIdtThreshold(String idtThreshold) {
        this.idtThreshold.setText(idtThreshold);
    }

    public String getIdtLatency() {
        return idtLatency.getText();
    }

    public void setIdtLatency(String idtLatency) {
        this.idtLatency.setText(idtLatency);
    }
}
