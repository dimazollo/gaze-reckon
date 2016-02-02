package serializer;

import main.Main;
import views.FDView;
import views.ParserView;
import views.RootLayoutView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author Dmitry Volovod
 * created on 14.10.2015
 */

public class Serializer {
    //saving & loading states of flags and other
    private static final String PROPERTIES_STORE = "gaze-reckon.properties";
    ParserView parserView;
    FDView fdView;
    RootLayoutView rootLayoutView;
    private Properties properties;

    public Serializer(Main mainApp) {
        properties = new Properties();
        parserView = mainApp.getParserView();
        fdView = mainApp.getFdView();
        rootLayoutView = mainApp.getRootLayoutView();
    }

    public boolean load() {
        File file = new File(PROPERTIES_STORE);
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                properties.loadFromXML(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            try {
                //text fields
                parserView.setTrackerFileAddress((String) properties.get("TrackerFileAddress"));
                parserView.setTestFileAddress((String) properties.get("TestFileAddress"));
                parserView.setOutputFileName((String) properties.get("OutputFileName"));
                //flags
                parserView.setNumber(properties.get("Number").equals("True"));
                parserView.setTime(properties.get("Time").equals("True"));
                parserView.setStimulusPosition(properties.get("StimulusPosition").equals("True"));
                parserView.setFix(properties.get("Fix").equals("True"));
                parserView.setAvg(properties.get("Avg").equals("True"));
                parserView.setRaw(properties.get("Raw").equals("True"));
                parserView.setDAvg(properties.get("dAvg").equals("True"));
                parserView.setdRaw(properties.get("dRaw").equals("True"));
                parserView.setlEyeAvg(properties.get("lEyeAvg").equals("True"));
                parserView.setlEyeRaw(properties.get("lEyeRaw").equals("True"));
                parserView.setlEyeDAvg(properties.get("lEyeDAvg").equals("True"));
                parserView.setlEyeDRaw(properties.get("lEyeDRaw").equals("True"));
                parserView.setlEyePCenter(properties.get("lEyePCenter").equals("True"));
                parserView.setlEyePSize(properties.get("lEyePSize").equals("True"));
                parserView.setrEyeAvg(properties.get("rEyeAvg").equals("True"));
                parserView.setrEyeRaw(properties.get("rEyeRaw").equals("True"));
                parserView.setrEyeDAvg(properties.get("rEyeDAvg").equals("True"));
                parserView.setrEyeDRaw(properties.get("rEyeDRaw").equals("True"));
                parserView.setrEyePCenter(properties.get("rEyePCenter").equals("True"));
                parserView.setrEyePSize(properties.get("rEyePSize").equals("True"));
                parserView.setTimeStamp(properties.get("TimeStamp").equals("True"));
                parserView.setCategory(properties.get("Category").equals("True"));
                parserView.setRequest(properties.get("Request").equals("True"));
                parserView.setState(properties.get("State").equals("True"));
                parserView.setStatuscode(properties.get("Statuscode").equals("True"));
                //fd elements states
                fdView.getIdt().setSelected(properties.get("IDT").equals("True"));
                fdView.setIdtLatency((String) properties.get("IDT_latency"));
                fdView.setIdtThreshold((String) properties.get("IDT_threshold"));
                fdView.getHyperbola().setSelected(properties.get("Hyperbola").equals("True"));
                fdView.getLogarithm().setSelected(properties.get("Logarithm").equals("True"));
                //RootLayout (Menu) elements states
                rootLayoutView.setLinearInterpolationFlag(properties.get("LinearSmoothingFilter").equals("True"));
                rootLayoutView.setListwiseDeletionFlag(properties.get("ListwiseDeletion").equals("True"));
                rootLayoutView.setSimpleRecoveryFlag(properties.get("SimpleRecovery").equals("True"));
                rootLayoutView.setCommaFlag(properties.get("CommaSeparator").equals("True"));
                rootLayoutView.setSemicolonFlag(properties.get("SemicolonSeparator").equals("True"));
                rootLayoutView.setUseConfigFlag(true);
                return true;
            } catch (NullPointerException ex) {
                file.delete();
                return false;
            }
        } else {
            return false;
        }
    }

    public void store() {
        if (rootLayoutView.getUseConfigFlag()) {
            //parser elements states
            //text fields
            properties.put("TrackerFileAddress", parserView.getTrackerFileAddress());
            properties.put("TestFileAddress", parserView.getTestFileAddress());
            properties.put("OutputFileName", parserView.getOutputFileName());
            //check boxes
            properties.put("Number", parserView.getNumber() ? "True" : "False");
            properties.put("Time", parserView.getTime() ? "True" : "False");
            properties.put("StimulusPosition", parserView.getStimulusPosition() ? "True" : "False");
            properties.put("Fix", parserView.getFix() ? "True" : "False");
            properties.put("Avg", parserView.getAvg() ? "True" : "False");
            properties.put("Raw", parserView.getRaw() ? "True" : "False");
            properties.put("dAvg", parserView.getdAvg() ? "True" : "False");
            properties.put("dRaw", parserView.getdRaw() ? "True" : "False");
            properties.put("lEyeAvg", parserView.getlEyeAvg() ? "True" : "False");
            properties.put("lEyeRaw", parserView.getlEyeRaw() ? "True" : "False");
            properties.put("lEyeDAvg", parserView.getlEyeDAvg() ? "True" : "False");
            properties.put("lEyeDRaw", parserView.getlEyeDRaw() ? "True" : "False");
            properties.put("lEyePCenter", parserView.getlEyePCenter() ? "True" : "False");
            properties.put("lEyePSize", parserView.getlEyePSize() ? "True" : "False");
            properties.put("rEyeAvg", parserView.getrEyeAvg() ? "True" : "False");
            properties.put("rEyeRaw", parserView.getrEyeRaw() ? "True" : "False");
            properties.put("rEyeDAvg", parserView.getrEyeDAvg() ? "True" : "False");
            properties.put("rEyeDRaw", parserView.getrEyeDRaw() ? "True" : "False");
            properties.put("rEyePCenter", parserView.getrEyePCenter() ? "True" : "False");
            properties.put("rEyePSize", parserView.getrEyePSize() ? "True" : "False");
            properties.put("TimeStamp", parserView.getTimeStamp() ? "True" : "False");
            properties.put("Category", parserView.getCategory() ? "True" : "False");
            properties.put("Request", parserView.getRequest() ? "True" : "False");
            properties.put("State", parserView.getState() ? "True" : "False");
            properties.put("Statuscode", parserView.getStatuscode() ? "True" : "False");
            //fd elements states
            properties.put("IDT", fdView.getIdt().isSelected() ? "True" : "False");
            properties.put("IDT_latency", fdView.getIdtLatency());
            properties.put("IDT_threshold", fdView.getIdtThreshold());
            properties.put("Hyperbola", fdView.getHyperbola().isSelected() ? "True" : "False");
            properties.put("Logarithm", fdView.getLogarithm().isSelected() ? "True" : "False");
            //RootLayout (Menu) elements states
            properties.put("LinearSmoothingFilter", rootLayoutView.getLinearInterpolationFlag() ? "True" : "False");
            properties.put("ListwiseDeletion", rootLayoutView.getListwiseDeletionFlag() ? "True" : "False");
            properties.put("SimpleRecovery", rootLayoutView.getSimpleRecoveryFlag() ? "True" : "False");
            properties.put("CommaSeparator", rootLayoutView.getCommaFlag() ? "True" : "False");
            properties.put("SemicolonSeparator", rootLayoutView.getSemicolonFlag() ? "True" : "False");
            //saving to file
            try {
                File file = new File(PROPERTIES_STORE);
                if (!file.exists()) {
                    file.createNewFile();
                }
                file.setWritable(true);
                FileOutputStream out = new FileOutputStream(file);
                properties.storeToXML(out, "File contains properties of Gaze Reckon application. " +
                        "Authors does not responsible for the correct operation of the program with changed configuration file." +
                        "Make changes at your own risk.");
                out.close();
                file.setReadOnly();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(PROPERTIES_STORE);
            if (file.exists()) file.delete();
        }
    }
}