package views;

import com.google.gson.JsonSyntaxException;
import controllers.DataController;
import dataRecovery.DataRecovery;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.eyetracker.Message;
import model.test.Stimulus;
import serializer.Serializer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class ParserView {
    //Reference to main class
    private final Property<Main> mainApp = new SimpleObjectProperty<>();
    private DataController dataController;
    private FDView fdView;
    private RootLayoutView rootLayoutView;
    @FXML
    private TextField trackerFileAddress;
    @FXML
    private TextField testFileAddress;
    @FXML
    private TextField outputFileName;
    @FXML
    private CheckBox number;
    @FXML
    private CheckBox time;
    @FXML
    private CheckBox stimulusPosition;
    @FXML
    private CheckBox fix;
    @FXML
    private CheckBox avg;
    @FXML
    private CheckBox raw;
    @FXML
    private CheckBox dAvg;
    @FXML
    private CheckBox dRaw;
    //leftEye
    @FXML
    private CheckBox lEyeAvg;
    @FXML
    private CheckBox lEyeRaw;
    @FXML
    private CheckBox lEyeDAvg;
    @FXML
    private CheckBox lEyeDRaw;
    @FXML
    private CheckBox lEyePCenter;
    @FXML
    private CheckBox lEyePSize;
    //rightEye
    @FXML
    private CheckBox rEyeAvg;
    @FXML
    private CheckBox rEyeRaw;
    @FXML
    private CheckBox rEyeDAvg;
    @FXML
    private CheckBox rEyeDRaw;
    @FXML
    private CheckBox rEyePCenter;
    @FXML
    private CheckBox rEyePSize;
    @FXML
    private CheckBox timeStamp;
    @FXML
    private CheckBox category;
    @FXML
    private CheckBox request;
    @FXML
    private CheckBox state;
    @FXML
    private CheckBox statuscode;
    //Buttons
    @FXML
    private Button chTrackerFile;
    @FXML
    private Button clearTrackerFile;
    @FXML
    private Button chTestFile;
    @FXML
    private Button clearTestFile;
    @FXML
    private Button start;
    @FXML
    private Button exit;

    public String getTrackerFileAddress() {
        return trackerFileAddress.getText();
    }

    public void setTrackerFileAddress(String trackerFileAddress) {
        if (trackerFileAddress == null) trackerFileAddress = "";
        this.trackerFileAddress.setText(trackerFileAddress);
    }

    public String getTestFileAddress() {
        return testFileAddress.getText();
    }

    public void setTestFileAddress(String testFileAddress) {
        if (testFileAddress == null) testFileAddress = "";
        this.testFileAddress.setText(testFileAddress);
    }

    public String getOutputFileName() {
        return outputFileName.getText();
    }

    public void setOutputFileName(String outputFileName) {
        if (outputFileName == null) outputFileName = "";
        this.outputFileName.setText(outputFileName);
    }

    public void setFDViewController(FDView fdView) {
        this.fdView = fdView;
    }

    @FXML
    private void initialize() {
        number.setSelected(false);
        timeStamp.setSelected(true);
        stimulusPosition.setSelected(true);
        fix.setSelected(true);

        avg.setSelected(true);
        raw.setSelected(false);
        dAvg.setSelected(true);
        dRaw.setSelected(false);

        //leftEye
        lEyeAvg.setSelected(false);
        lEyeRaw.setSelected(false);
        lEyeDAvg.setSelected(false);
        lEyeDRaw.setSelected(false);
        lEyePCenter.setSelected(false);
        lEyePSize.setSelected(false);

        //rightEye
        rEyeAvg.setSelected(false);
        rEyeRaw.setSelected(false);
        rEyeDAvg.setSelected(false);
        rEyeDRaw.setSelected(false);
        rEyePCenter.setSelected(false);
        rEyePSize.setSelected(false);

        time.setSelected(false);
        category.setSelected(false);
        request.setSelected(false);
        state.setSelected(false);
        statuscode.setSelected(false);

        trackerFileAddress.setText("");
        testFileAddress.setText("");
        outputFileName.setText("");

        //eventListener which enable/disable some gui elements according to presence of chosen files
        trackerFileAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.equals("")) {
                    clearTrackerFile.setDisable(true);
                } else {
                    clearTrackerFile.setDisable(false);
                    File file = new File(newValue);
                    setTrackerFile(file);
                }
            } catch (NullPointerException ex) {
                //possible error while loading saved state
                System.err.format("Exception: %s%n", ex);
                Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //eventListener which enable/disable some gui elements according to presence of chosen files
        testFileAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.equals("")) {
                    clearTestFile.setDisable(true);
                    stimulusPosition.setDisable(true);
                    dAvg.setDisable(true);
                    dRaw.setDisable(true);
                    lEyeDAvg.setDisable(true);
                    lEyeDRaw.setDisable(true);
                    rEyeDAvg.setDisable(true);
                    rEyeDRaw.setDisable(true);
                } else {
                    File testFile = new File(newValue);
                    setTestFile(testFile);
                    stimulusPosition.setDisable(false);
                    dAvg.setDisable(false);
                    dRaw.setDisable(false);
                    lEyeDAvg.setDisable(false);
                    lEyeDRaw.setDisable(false);
                    rEyeDAvg.setDisable(false);
                    rEyeDRaw.setDisable(false);
                    clearTestFile.setDisable(false);
                }
            } catch (NullPointerException ex) {
                //possible error while loading saved state
                System.err.format("Exception: %s%n", ex);
                Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @FXML
    private void handleCheckBox() {
    }

    public void setMainApp(Main mainApp) {
        this.mainApp.setValue(mainApp);
        dataController = DataController.getInstance(this.mainApp);
        //Warning - Setting of fdViewController before it have been initialized in Main causes NullPointerException;
        fdView = mainApp.getFdView();
        rootLayoutView = mainApp.getRootLayoutView();
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*"));
        Stage stage = new Stage();
        return fileChooser.showOpenDialog(stage);
    }

    @FXML
    private void handleExtBtn() {
        Serializer deserializer = new Serializer(mainApp.getValue());
        deserializer.store();
        System.exit(0);

    }

    @FXML
    private void handleChTrackerFileBtn() {
        setTrackerFile(chooseFile());
    }

    private void setTrackerFile(File trackerFile) {
        dataController.setTrackerFile(trackerFile);
        if (dataController.getTrackerFile() != null) {
            trackerFileAddress.setText(dataController.getTrackerFile().getAbsolutePath());
            ArrayList<Message> messages = null;
            try {
                messages = dataController.parseMessages(dataController.getTrackerFile());
            } catch (JsonSyntaxException e) {
                Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, e);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Wrong data!");
                alert.setContentText("Unable to parse the file specified.");
                alert.showAndWait();
                messages = null;
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Read error");
                alert.setContentText("Error occurred while \"Read\" operation.\n" +
                        "The file does not exist or being used by another program.");
                alert.showAndWait();
                System.err.format("Exception: %s%n", e);
                Logger.getLogger(DataController.class.getName()).log(Level.SEVERE, null, e);
            }
            String trackerName = dataController.getTrackerFile().getName().substring(0, dataController.getTrackerFile().getName().lastIndexOf(".")); //Tracker file name without extension
            if (outputFileName.getText().equals("")) {
                outputFileName.setText(trackerName + "_parsed");
            }
            mainApp.getValue().setMessages(messages);
        }
    }


    @FXML
    private void handleClearTrackerFileBtn() {
        dataController.setTrackerFile(null);
        trackerFileAddress.setText("");
        outputFileName.setText("");
    }

    @FXML
    private void handleChTestFileBtn() {
        setTestFile(chooseFile());
    }

    private void setTestFile(File testFile) {
        dataController.setTestFile(testFile);
        if (dataController.getTestFile() != null) {
            try {
                ArrayList<Stimulus> stimuli = dataController.parseStimuli(dataController.getTestFile());
                if (stimuli == null || stimuli.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Empty file");
                    alert.setContentText("Specified file contains no data.");
                    alert.showAndWait();
                } else {
                    mainApp.getValue().setStimuli(stimuli);
                }
            } catch (ParseException | IndexOutOfBoundsException ex) {
                Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Wrong data file");
                alert.setContentText("File consists wrong data. Please choose correct file.");
                alert.showAndWait();
                mainApp.getValue().setStimuli(null);
            } catch (IOException ex) {
                Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, null, ex);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Read error");
                alert.setContentText("Error occurred while \"Read\" operation.\n" +
                        "The file does not exist or being used by another program.");
                alert.showAndWait();
                mainApp.getValue().setStimuli(null);
            }
            if (mainApp.getValue().getStimuli() != null) {
                testFileAddress.setText(dataController.getTestFile().getAbsolutePath());
            }
        }
    }

    @FXML
    private void handleClearTestFileBtn() {
        dataController.setTestFile(null);
        testFileAddress.setText("");
//        //TODO - disable by eventListener
//        stimulusPosition.setDisable(true);
//        dAvg.setDisable(true);
//        dRaw.setDisable(true);
//        lEyeDAvg.setDisable(true);
//        lEyeDRaw.setDisable(true);
//        rEyeDAvg.setDisable(true);
//        rEyeDRaw.setDisable(true);
//        clearTestFile.setDisable(true);
    }

    @FXML
    void handleStartBtn() {
        try {
            File outputFile;
            if (dataController.getTrackerFile() == null) {
                //TODO - move Alerts invocations into separate method.
                // Maybe making a class responsible for error handling would be wise decision
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("File not specified");
                alert.setContentText("Please specify eye-tracker data file.");
                alert.showAndWait();
                return;
            } else if (outputFileName.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("File name not specified");
                alert.setContentText("Please specify the name of output file.");
                alert.showAndWait();
                return;
            } else {
                String trackerFilePath = dataController.getTrackerFile().getAbsolutePath().substring(0,  //Path to the trackerFile;
                        dataController.getTrackerFile().getAbsolutePath().lastIndexOf(File.separator));
                outputFile = new File(trackerFilePath + File.separator + outputFileName.getText() + ".csv");
            }

            if (outputFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText(null);
                alert.setContentText("File with the same name already exists!\n" + "Rewrite?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK) {
                    return;
                }
            } else {
                if (!outputFile.createNewFile()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Write error");
                    alert.setContentText("No permission to create the file.");
                    alert.showAndWait();
                }
            }
            DataRecovery.correctRepetitive(mainApp.getValue().getMessages());
            if (rootLayoutView.getSimpleRecoveryFlag()) {
                DataRecovery.oneEyeMissRecovery(mainApp.getValue().getMessages());       //restores missing data
            }
            if (rootLayoutView.getLinearInterpolationFlag()) {
                DataRecovery.interpolationRecovery(mainApp.getValue().getMessages());       //restores missing data
            }
            if (rootLayoutView.getListwiseDeletionFlag()) {
                DataRecovery.listwiseDeletion(mainApp.getValue().getMessages());
            }

            LinkedHashMap<String, CheckBox> firedFlags = getFiredFlags(); //consists of names and related flags
            dataController.write(firedFlags, mainApp.getValue().getMessages(), outputFile);   //method of actual output to the file

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Writing complete");
            alert.setContentText("The operation performed successfully.");
            alert.showAndWait();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Write error");
            alert.setContentText("Error occurred while \"Write\" operation.\n" +
                    "Output file being used by another program or write operation not allowed.");
            alert.showAndWait();
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, null, ex);
            System.err.format("IOException: %s%n", ex);
        }
    }

    //getFiredFlags returns a hashMap from where every key represents a name of corresponding flag and every value reflects its state (fired or not)
    private LinkedHashMap<String, CheckBox> getFiredFlags() {
        LinkedHashMap<String, CheckBox> linkedHashMap = new LinkedHashMap<>();
        Boolean testFileSelected = (dataController.getTestFile() != null);
        if (getNumber()) linkedHashMap.put(Message.NUMBER, number);
        if (getTimeStamp()) linkedHashMap.put(Message.TIMESTAMP, timeStamp);
        if (getStimulusPosition() && testFileSelected) linkedHashMap.put(Stimulus.POSITION, stimulusPosition);
        if (getFix()) linkedHashMap.put(Message.FIX, fix);
        if (fdView.getIdt().isSelected() && testFileSelected)
            linkedHashMap.put(FDView.IDT, fdView.getIdt());
        if (fdView.getHyperbola().isSelected() && testFileSelected)
            linkedHashMap.put(FDView.HYPERBOLA, fdView.getHyperbola());
        if (fdView.getLogarithm().isSelected() && testFileSelected)
            linkedHashMap.put(FDView.LNX, fdView.getLogarithm());
        if (getAvg()) linkedHashMap.put(Message.AVERAGE, avg);
        if (getRaw()) linkedHashMap.put(Message.RAW, raw);
        if (getdAvg() && testFileSelected) linkedHashMap.put(Message.DELTA_AVERAGE, dAvg);
        if (getdRaw() && testFileSelected) linkedHashMap.put(Message.DELTA_RAW, dRaw);
        if (getlEyeAvg()) linkedHashMap.put(Message.LEFT_EYE_AVG, lEyeAvg);
        if (getlEyeRaw()) linkedHashMap.put(Message.LEFT_EYE_RAW, lEyeRaw);
        if (getlEyeDAvg() && testFileSelected) linkedHashMap.put(Message.LEFT_EYE_DELTA_AVG, lEyeDAvg);
        if (getlEyeDRaw() && testFileSelected) linkedHashMap.put(Message.LEFT_EYE_DELTA_RAW, lEyeDRaw);
        if (getlEyePCenter()) linkedHashMap.put(Message.LEFT_EYE_PCENTER, lEyePCenter);
        if (getlEyePSize()) linkedHashMap.put(Message.LEFT_EYE_PSIZE, lEyePSize);
        if (getrEyeAvg()) linkedHashMap.put(Message.RIGHT_EYE_AVG, rEyeAvg);
        if (getrEyeRaw()) linkedHashMap.put(Message.RIGHT_EYE_RAW, rEyeRaw);
        if (getrEyeDAvg() && testFileSelected) linkedHashMap.put(Message.RIGHT_EYE_DELTA_AVG, rEyeDAvg);
        if (getrEyeDRaw() && testFileSelected) linkedHashMap.put(Message.RIGHT_EYE_DELTA_RAW, rEyeDRaw);
        if (getrEyePCenter()) linkedHashMap.put(Message.RIGHT_EYE_PCENTER, rEyePCenter);
        if (getrEyePSize()) linkedHashMap.put(Message.RIGHT_EYE_PSIZE, rEyePSize);
        if (getTime()) linkedHashMap.put(Message.TIME, time);
        if (getCategory()) linkedHashMap.put(Message.CATEGORY, category);
        if (getRequest()) linkedHashMap.put(Message.REQUEST, request);
        if (getState()) linkedHashMap.put(Message.STATE, state);
        if (getStatuscode()) linkedHashMap.put(Message.STATUSCODE, statuscode);
        return linkedHashMap;
    }

    public boolean getNumber() {
        return number.isSelected();
    }

    public void setNumber(Boolean number) {
        this.number.setSelected(number);
    }

    public boolean getTime() {
        return time.isSelected();
    }

    public void setTime(Boolean time) {
        this.time.setSelected(time);
    }

    public boolean getStimulusPosition() {
        return stimulusPosition.isSelected();
    }

    public void setStimulusPosition(Boolean stimulusPosition) {
        this.stimulusPosition.setSelected(stimulusPosition);
    }

    public boolean getFix() {
        return fix.isSelected();
    }

    public void setFix(Boolean flag) {
        this.fix.setSelected(flag);
    }

    public boolean getAvg() {
        return avg.isSelected();
    }

    public void setAvg(Boolean flag) {
        this.avg.setSelected(flag);
    }

    public boolean getRaw() {
        return raw.isSelected();
    }

    public void setRaw(Boolean flag) {
        this.raw.setSelected(flag);
    }

    public boolean getdAvg() {
        return dAvg.isSelected();
    }

    public boolean getdRaw() {
        return dRaw.isSelected();
    }

    public void setdRaw(Boolean flag) {
        this.dRaw.setSelected(flag);
    }

    public boolean getlEyeAvg() {
        return lEyeAvg.isSelected();
    }

    public void setlEyeAvg(Boolean flag) {
        this.lEyeAvg.setSelected(flag);
    }

    public boolean getlEyeRaw() {
        return lEyeRaw.isSelected();
    }

    public void setlEyeRaw(Boolean flag) {
        this.lEyeRaw.setSelected(flag);
    }

    public boolean getlEyeDAvg() {
        return lEyeDAvg.isSelected();
    }

    public void setlEyeDAvg(Boolean flag) {
        this.lEyeDAvg.setSelected(flag);
    }

    public boolean getlEyeDRaw() {
        return lEyeDRaw.isSelected();
    }

    public void setlEyeDRaw(Boolean flag) {
        this.lEyeDAvg.setSelected(flag);
    }

    public boolean getlEyePCenter() {
        return lEyePCenter.isSelected();
    }

    public void setlEyePCenter(Boolean flag) {
        this.lEyePCenter.setSelected(flag);
    }

    public boolean getlEyePSize() {
        return lEyePSize.isSelected();
    }

    public void setlEyePSize(Boolean flag) {
        this.lEyePSize.setSelected(flag);
    }

    public boolean getrEyeAvg() {
        return rEyeAvg.isSelected();
    }

    public void setrEyeAvg(Boolean flag) {
        this.rEyeAvg.setSelected(flag);
    }

    public boolean getrEyeRaw() {
        return rEyeRaw.isSelected();
    }

    public void setrEyeRaw(Boolean flag) {
        this.rEyeRaw.setSelected(flag);
    }

    public boolean getrEyeDAvg() {
        return rEyeDAvg.isSelected();
    }

    public void setrEyeDAvg(Boolean flag) {
        this.rEyeDAvg.setSelected(flag);
    }

    public boolean getrEyeDRaw() {
        return rEyeDRaw.isSelected();
    }

    public void setrEyeDRaw(Boolean flag) {
        this.rEyeDAvg.setSelected(flag);
    }

    public boolean getrEyePCenter() {
        return rEyePCenter.isSelected();
    }

    public void setrEyePCenter(Boolean flag) {
        this.rEyePCenter.setSelected(flag);
    }

    public boolean getrEyePSize() {
        return rEyePSize.isSelected();
    }

    public void setrEyePSize(Boolean flag) {
        this.rEyePSize.setSelected(flag);
    }

    public boolean getTimeStamp() {
        return timeStamp.isSelected();
    }

    public void setTimeStamp(Boolean flag) {
        this.timeStamp.setSelected(flag);
    }

    public boolean getCategory() {
        return category.isSelected();
    }

    public void setCategory(Boolean flag) {
        this.category.setSelected(flag);
    }

    public boolean getRequest() {
        return request.isSelected();
    }

    public void setRequest(Boolean flag) {
        this.request.setSelected(flag);
    }

    public boolean getState() {
        return state.isSelected();
    }

    public void setState(Boolean flag) {
        this.state.setSelected(flag);
    }

    public boolean getStatuscode() {
        return statuscode.isSelected();
    }

    public void setStatuscode(Boolean flag) {
        this.statuscode.setSelected(flag);
    }

    public void setDAvg(Boolean flag) {
        this.dAvg.setSelected(flag);
    }
}
