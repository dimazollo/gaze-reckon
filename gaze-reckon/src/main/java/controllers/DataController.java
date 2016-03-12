package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fd.FixMethods;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import main.Main;
import model.MappedDataItem;
import model.eyetracker.Message;
import model.test.Stimulus;
import views.FDView;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * @Author Dmitry Volovod
 * created on 16.10.2015
 */
public class DataController implements Writable {
    private static DataController instance;
    private final Property<Main> mainApp;
    //input files
    private File trackerFile;
    private File testFile;

    private String fieldSeparator;

    private DataController(Property<Main> mainApp) {
        this.mainApp = new SimpleObjectProperty<>();
        this.mainApp.bind(mainApp);
        mainApp.getValue().getRootLayoutView();
    }

    public static DataController getInstance(Property<Main> mainApp) {
        if (instance == null) {
            instance = new DataController(mainApp);
        }
        return instance;
    }

    // parseStimuli parses a set of strings from resulting file of Regina's test.
    public ArrayList<Stimulus> parseStimuli(File testFile) throws ParseException, IOException, IndexOutOfBoundsException {
        ArrayList<Stimulus> stimuli = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(testFile));
        String s = bufferedReader.readLine();
        if (!s.matches("^\\D*$")) {// Regex matches numbers (check if the string is a header).
            stimuli.add(new Stimulus(s));
        }
        while (bufferedReader.ready()) {
            s = bufferedReader.readLine();
            stimuli.add(new Stimulus(s));
        }
        return stimuli;
    }

    //parseMessages parses a set of JSON strings from resulting eye-tracker's file.
    public ArrayList<Message> parseMessages(File trackerFile) throws IOException {
        ArrayList<Message> messages = new ArrayList<>();//mainApp.getMessages();

        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Gson gson = gb.create();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(trackerFile));
        String s;
        while (bufferedReader.ready()) {
            s = bufferedReader.readLine();
            Message message = gson.fromJson(s, Message.class);
            if (message.category.equals("tracker")) {
                messages.add(message);
            }
        }
        return messages;
    }

    // mapTrackersAndStimuli builds a HashMap of tracker messages and related stimuli in compliance with their time.
    public static LinkedHashMap<Message, Stimulus> mapTrackersAndStimuli(ArrayList<Message> messages, ArrayList<Stimulus> stimuli) {
        Date stimulusStart = new Date();
        Date stimulusEnd = new Date();
        Date presentingTime = new Date();
        if (stimuli.size() == 0) return null;
        if (stimuli.size() == 1) {
            presentingTime.setTime(stimuli.get(0).getTimestamp().getTime() + 5000);
        } else {
            presentingTime.setTime(stimuli.get(1).getTimestamp().getTime() - stimuli.get(0).getTimestamp().getTime());
        }
        int i = 0;
        LinkedHashMap<Message, Stimulus> mappedData = new LinkedHashMap<>();
        for (Stimulus stimulus : stimuli) {
            stimulusStart.setTime(stimulus.getTimestamp().getTime());
            stimulusEnd.setTime(stimulusStart.getTime() + presentingTime.getTime());
            while (messages.get(i).values.frame.timestamp.before(stimulusStart)) {
                //mappedData.put(messages.get(i), null);  //write messages before the first stimulus have been demonstrated
                i++;
            }
            while (messages.get(i).values.frame.timestamp.before(stimulusEnd)) {
                mappedData.put(messages.get(i), stimulus);
                i++;
            }
        }
//        while (i < messages.size()) {
//            mappedData.put(messages.get(i), null); //write messages after the last stimulus have been demonstrated
//            i++;
//        }
        return mappedData;
    }


    //TODO-Dmitry - Возможно стоит перейти на такой вид представления данных вместо LinkedHashMap.
    //TODO - Метод не тестировали.
    public static ArrayList<MappedDataItem> createMappedData(ArrayList<Message> messages, ArrayList<Stimulus> stimuli) {
        Date stimulusStart = new Date();
        Date stimulusEnd = new Date();
        Date presentingTime = new Date();
        if (stimuli.size() == 0) return null;
        if (stimuli.size() == 1) {
            presentingTime.setTime(stimuli.get(0).getTimestamp().getTime() + 5000);
        } else {
            presentingTime.setTime(stimuli.get(1).getTimestamp().getTime() - stimuli.get(0).getTimestamp().getTime());
        }
        int i = 0;
        ArrayList<MappedDataItem> mappedDataArrayList = new ArrayList<>();
        for (Stimulus stimulus : stimuli) {
            stimulusStart.setTime(stimulus.getTimestamp().getTime());
            stimulusEnd.setTime(stimulusStart.getTime() + presentingTime.getTime());
            while (messages.get(i).values.frame.timestamp.before(stimulusStart)) {
                //mappedData.put(messages.get(i), null);  //write messages before the first stimulus have been demonstrated
                i++;
            }
            ArrayList<Message> relatedMessages = new ArrayList<>();
            while (messages.get(i).values.frame.timestamp.before(stimulusEnd)) {
                relatedMessages.add(messages.get(i));
                i++;
            }
            mappedDataArrayList.add(new MappedDataItem(stimulus, relatedMessages));
        }
        return mappedDataArrayList;
    }

    // "write" aggregates all "write" methods and so outputs in file all selected fields of parsed tracker messages, information about
    //presented stimuli and related computed data obtained by different algorithms
    public void write(LinkedHashMap<String, CheckBox> firedFlags, ArrayList<Message> messages, File csvFile) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        String s = "";  //header of table
        fieldSeparator = mainApp.getValue().getRootLayoutView().getFieldSeparator();
        for (Map.Entry<String, CheckBox> entry : firedFlags.entrySet()) {
            if (entry.getKey().equals(Message.NUMBER)
                    || entry.getKey().equals(Message.TIMESTAMP)
                    || entry.getKey().equals(Message.FIX)
                    || entry.getKey().equals(Message.LEFT_EYE_PSIZE)
                    || entry.getKey().equals(Message.RIGHT_EYE_PSIZE)
                    || entry.getKey().equals(Message.TIME)
                    || entry.getKey().equals(Message.CATEGORY)
                    || entry.getKey().equals(Message.REQUEST)
                    || entry.getKey().equals(Message.STATE)
                    || entry.getKey().equals(Message.STATUSCODE)
                    || entry.getKey().equals(FDView.IDT)
                    || entry.getKey().equals(FDView.HYPERBOLA)
                    || entry.getKey().equals(FDView.LNX)
                    || entry.getKey().contains("delta")) {
                s = s.concat(entry.getKey() + fieldSeparator);
            } else {
                s = s.concat(entry.getKey() + " x" + fieldSeparator);
                s = s.concat(entry.getKey() + " y" + fieldSeparator);
            }
        }
        s = s.concat(System.lineSeparator());
        bufferedWriter.write(s);
        if (testFile == null) {
            //writing of data in case of unselected test file
            writeTrackerData(firedFlags, messages, bufferedWriter);
        } else {
            //TODO - All of that should be revised
            LinkedHashMap<Message, Stimulus> mappedData = mapTrackersAndStimuli(messages, mainApp.getValue().getStimuli());
            if (mainApp.getValue().getFdView().getIdt().isSelected()) {
                //map tracker data with test and IDT
                writeMappedIDTData(firedFlags, mappedData, Message.DELTA_AVERAGE, bufferedWriter);
            } else {
                //map tracker data with test data
                writeMappedData(firedFlags, mappedData, bufferedWriter);
            }
        }
        bufferedWriter.close();
    }

    //writeTrackerData writes in file all selected fields of messages obtained only from the eye-tracker
    private void writeTrackerData(LinkedHashMap<String, CheckBox> firedFlags, ArrayList<Message> messages, BufferedWriter bufferedWriter) throws IOException {
        java.lang.Integer counter = 1;
        for (Message message : messages) {
            String s = "";
            HashMap<String, String> strings = message.toStringHashMap(fieldSeparator);
            strings.put("#", counter.toString());
            for (String key : firedFlags.keySet()) {
                s = s.concat(strings.get(key) + fieldSeparator);
            }
            s = s.concat(System.lineSeparator());
            bufferedWriter.write(s);
            counter++;
        }
    }

    //writeMappedData writes in file all selected fields stimuli from test file and related tracker messages
    private void writeMappedData(LinkedHashMap<String, CheckBox> firedFlags, LinkedHashMap<Message, Stimulus> mappedData, BufferedWriter bufferedWriter) throws IOException {
        Integer counter = 1;
        for (Map.Entry<Message, Stimulus> entry : mappedData.entrySet()) {
            if (entry.getValue() != null) {
                String s = "";
                HashMap<String, String> row = stringsAggregate(entry.getKey(), entry.getValue());
                row.put("#", counter.toString());
                for (String key : firedFlags.keySet()) {
                    s = s.concat(row.get(key) + fieldSeparator);
                }
                s = s.concat(System.lineSeparator());
                bufferedWriter.write(s);
                counter++;
            }
        }
    }

    //writeMappedIDTData do same things as previous method "writeMappedData" + writes fixations info computed by IDT method;
    //TODO talk about writeMappedIDTData and writeMappedData because they are almost the same
    private void writeMappedIDTData(LinkedHashMap<String, CheckBox> firedFlags, LinkedHashMap<Message, Stimulus> mappedData, String column, BufferedWriter bufferedWriter) throws IOException {
        Integer counter = 1;
        LinkedList<HashMap<String, Boolean>> fixationsList = getFixationsList(column, mappedData);
        for (Map.Entry<Message, Stimulus> entry : mappedData.entrySet()) {
            HashMap<String, Boolean> fixations = fixationsList.get(counter - 1);
            if (entry.getValue() != null) {
                String s = "";
                HashMap<String, String> row = stringsAggregate(entry.getKey(), entry.getValue());
                row.put(FDView.IDT, fixations.get(FDView.IDT).toString());
//                row.put(FDViewController.HYPERBOLA, fixations.get(FDViewController.HYPERBOLA).toString());
//                row.put(FDViewController.LNX, fixations.get(FDViewController.LNX).toString());
                row.put("#", counter.toString());
                for (String key : firedFlags.keySet()) {
                    s = s.concat(row.get(key) + fieldSeparator);
                }
                s = s.concat(System.lineSeparator());
                bufferedWriter.write(s);
                counter++;
            }
        }
    }

    //TODO here was done bad changes when several strings of code was deleted. It should be returned.
    // Now instead of LinkedList<<HashMap<String, Boolean>> it is able to use only Pair<String, Boolean>;
    private LinkedList<HashMap<String, Boolean>> getFixationsList(String column, LinkedHashMap<Message, Stimulus> mappedData) {
        FDView fdvc = mainApp.getValue().getFdView();
        ArrayList<ArrayList<Double>> arrayOfDistances = computeDistances(column, mappedData);
        LinkedList<HashMap<String, Boolean>> list = new LinkedList<>();
        for (ArrayList<Double> distances : arrayOfDistances) {
            ArrayList<Boolean> idtList = FixMethods.idt(distances, fdvc.getIdtThresholdAsInt(), fdvc.getIdtLatencyAsInt());
            for (int i = 0; i < idtList.size(); i++) {
                HashMap<String, Boolean> fixSet = new HashMap<>();
                fixSet.put(FDView.IDT, idtList.get(i));
                System.out.println(fixSet);
                list.add(fixSet);
            }
        }
        return list;
    }

    // stringsAggregate puts together all messages (from tracker) and stimuli (from test) and computed deltas in single HashMap.
    private HashMap<String, String> stringsAggregate(Message msg, Stimulus stmls) {
        HashMap<String, String> row = msg.toStringHashMap(fieldSeparator);
        row.put(Message.DELTA_AVERAGE, msg.values.frame.avg.getDistance(stmls.getPosition()).toString());
        row.put(Message.DELTA_RAW, msg.values.frame.raw.getDistance(stmls.getPosition()).toString());
        row.put(Message.LEFT_EYE_DELTA_AVG, msg.values.frame.lefteye.avg.getDistance(stmls.getPosition()).toString());
        row.put(Message.LEFT_EYE_DELTA_RAW, msg.values.frame.lefteye.raw.getDistance(stmls.getPosition()).toString());
        row.put(Message.RIGHT_EYE_DELTA_AVG, msg.values.frame.righteye.avg.getDistance(stmls.getPosition()).toString());
        row.put(Message.RIGHT_EYE_DELTA_RAW, msg.values.frame.righteye.raw.getDistance(stmls.getPosition()).toString());
        row.put(Stimulus.POSITION, stmls.getPosition().getDescription(fieldSeparator));
        return row;
    }

    // getDeltas returns all "deltas" from single tracker message.
    private static HashMap<String, Double> getDeltas(Message msg, Stimulus stmls) {
        HashMap<String, Double> row = new HashMap<>();
        row.put(Message.DELTA_AVERAGE, msg.values.frame.avg.getDistance(stmls.getPosition()));
        row.put(Message.DELTA_RAW, msg.values.frame.raw.getDistance(stmls.getPosition()));
        row.put(Message.LEFT_EYE_DELTA_AVG, msg.values.frame.lefteye.avg.getDistance(stmls.getPosition()));
        row.put(Message.LEFT_EYE_DELTA_RAW, msg.values.frame.lefteye.raw.getDistance(stmls.getPosition()));
        row.put(Message.RIGHT_EYE_DELTA_AVG, msg.values.frame.righteye.avg.getDistance(stmls.getPosition()));
        row.put(Message.RIGHT_EYE_DELTA_RAW, msg.values.frame.righteye.raw.getDistance(stmls.getPosition()));
        return row;
    }

    //computeDistances returns a number of arrayLists of "delta" values related to every stimulus specified by column
    // for example computeDistances(Message.DELTA_AVERAGE, mappedData) will return only "deltaAverage" field's values;
    public static ArrayList<ArrayList<Double>> computeDistances(String column, LinkedHashMap<Message, Stimulus> mappedData) {
        ArrayList<ArrayList<Double>> arrayLists = new ArrayList<>();
        ArrayList<Double> doubleArrayList = new ArrayList<>();
        Stimulus current = mappedData.entrySet().iterator().next().getValue();
        assert mappedData != null;
        for (Map.Entry<Message, Stimulus> entry : mappedData.entrySet()) {
            HashMap<String, Double> deltas = getDeltas(entry.getKey(), entry.getValue());
            Double d = deltas.get(column);
            if (current.equals(entry.getValue())) {
                doubleArrayList.add(d);
            } else {
                arrayLists.add(doubleArrayList);
                doubleArrayList = new ArrayList<>();
                doubleArrayList.add(d);
                current = entry.getValue();
            }
        }
        arrayLists.add(doubleArrayList); //last of deltas array
        return arrayLists;
    }


    public File getTrackerFile() {
        return trackerFile;
    }

    public void setTrackerFile(File trackerFile) {
        this.trackerFile = trackerFile;
    }

    public File getTestFile() {
        return testFile;
    }

    public void setTestFile(File testFile) {
        this.testFile = testFile;
    }
}
