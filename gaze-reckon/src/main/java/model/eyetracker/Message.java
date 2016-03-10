package model.eyetracker;

import java.util.HashMap;

/**
 * @Author Dmitry Volovod
 * created on 10.09.2015
 */
public class Message {
    public static final String NUMBER = "#";
    public static final String TIMESTAMP = "timestamp";
    public static final String FIX = "fix";
    public static final String AVERAGE = "average";
    public static final String RAW = "raw";
    public static final String DELTA_AVERAGE = "delta average";
    public static final String DELTA_RAW = "delta raw";
    public static final String LEFT_EYE_AVG = "left eye average";
    public static final String LEFT_EYE_RAW = "left eye raw";
    public static final String LEFT_EYE_DELTA_AVG = "left eye delta average";
    public static final String LEFT_EYE_DELTA_RAW = "left eye delta raw";
    public static final String LEFT_EYE_PCENTER = "left eye pupil center";
    public static final String LEFT_EYE_PSIZE = "left eye pupil size";
    public static final String RIGHT_EYE_AVG = "right eye average";
    public static final String RIGHT_EYE_RAW = "right eye raw";
    public static final String RIGHT_EYE_DELTA_AVG = "right eye delta average";
    public static final String RIGHT_EYE_DELTA_RAW = "right eye delta raw";
    public static final String RIGHT_EYE_PCENTER = "right eye pupil center";
    public static final String RIGHT_EYE_PSIZE = "right eye pupil size";
    public static final String TIME = "time";
    public static final String CATEGORY = "category";
    public static final String REQUEST = "request";
    public static final String STATE = "state";
    public static final String STATUSCODE = "statuscode";

    public String category;
    public String request;
    public int statuscode;
    public Values values;

    public Message() {
        category = null;
        request = null;
        statuscode = 0;
        values = new Values();
    }

    public String hasMissingData() {
        return values.frame.hasMissingData();
        //return values
        //null : both eyes data is defined;
        //LEFT : left eye position is not defined;
        //RIGHT : right eye position is not defined;
        //BOTH : both eyes position is not defined;
    }

    public String toString() {
        return "category = " + category + System.lineSeparator() +
                "request = " + request + System.lineSeparator() +
                "statuscode = " + statuscode + System.lineSeparator() +
                "values (" + System.lineSeparator() +
                values.toString() + System.lineSeparator() + ")" + System.lineSeparator();
    }

    public String[] toStringArray(String fieldSeparator) {
        String[] array = new String[18];
        array[1] = "" + this.values.frame.time;
        array[2] = "" + this.values.frame.fix;
        array[3] = this.values.frame.avg.getDescription(fieldSeparator);
        array[4] = this.values.frame.raw.getDescription(fieldSeparator);
        array[5] = this.values.frame.lefteye.avg.getDescription(fieldSeparator);
        array[6] = this.values.frame.lefteye.raw.getDescription(fieldSeparator);
        array[7] = this.values.frame.lefteye.pcenter.getDescription(fieldSeparator);
        array[8] = "" + this.values.frame.lefteye.psize;
        array[9] = this.values.frame.righteye.avg.getDescription(fieldSeparator);
        array[10] = this.values.frame.righteye.raw.getDescription(fieldSeparator);
        array[11] = this.values.frame.righteye.pcenter.getDescription(fieldSeparator);
        array[12] = "" + this.values.frame.righteye.psize;
        array[13] = this.values.frame.getTimestampAsString();
        array[14] = category;
        array[15] = request;
        array[16] = "" + this.values.frame.state;
        array[17] = "" + statuscode;
        return array;
    }

    public HashMap<String, String> toStringHashMap(String fieldSeparator) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Message.TIME, "" + this.values.frame.time);
        hashMap.put(Message.FIX, "" + this.values.frame.fix);
        hashMap.put(Message.AVERAGE, this.values.frame.avg.getDescription(fieldSeparator));
        hashMap.put(Message.RAW, this.values.frame.raw.getDescription(fieldSeparator));
        hashMap.put(Message.LEFT_EYE_AVG, this.values.frame.lefteye.avg.getDescription(fieldSeparator));
        hashMap.put(Message.LEFT_EYE_RAW, this.values.frame.lefteye.raw.getDescription(fieldSeparator));
        hashMap.put(Message.LEFT_EYE_PCENTER, this.values.frame.lefteye.pcenter.getDescription(fieldSeparator));
        hashMap.put(Message.LEFT_EYE_PSIZE, "" + this.values.frame.lefteye.psize);
        hashMap.put(Message.RIGHT_EYE_AVG, this.values.frame.righteye.avg.getDescription(fieldSeparator));
        hashMap.put(Message.RIGHT_EYE_RAW, this.values.frame.righteye.raw.getDescription(fieldSeparator));
        hashMap.put(Message.RIGHT_EYE_PCENTER, this.values.frame.righteye.pcenter.getDescription(fieldSeparator));
        hashMap.put(Message.RIGHT_EYE_PSIZE, "" + this.values.frame.righteye.psize);
        hashMap.put(Message.TIMESTAMP, this.values.frame.getTimestampAsString());
        hashMap.put(Message.CATEGORY, category);
        hashMap.put(Message.REQUEST, request);
        hashMap.put(Message.STATE, "" + this.values.frame.state);
        hashMap.put(Message.STATUSCODE, "" + statuscode);
        return hashMap;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Message.TIME, this.values.frame.time);
        hashMap.put(Message.FIX, this.values.frame.fix);
        hashMap.put(Message.AVERAGE, this.values.frame.avg);
        hashMap.put(Message.RAW, this.values.frame.raw);
        hashMap.put(Message.LEFT_EYE_AVG, this.values.frame.lefteye.avg);
        hashMap.put(Message.LEFT_EYE_RAW, this.values.frame.lefteye.raw);
        hashMap.put(Message.LEFT_EYE_PCENTER, this.values.frame.lefteye.pcenter);
        hashMap.put(Message.LEFT_EYE_PSIZE, this.values.frame.lefteye.psize);
        hashMap.put(Message.RIGHT_EYE_AVG, this.values.frame.righteye.avg);
        hashMap.put(Message.RIGHT_EYE_RAW, this.values.frame.righteye.raw);
        hashMap.put(Message.RIGHT_EYE_PCENTER, this.values.frame.righteye.pcenter);
        hashMap.put(Message.RIGHT_EYE_PSIZE, this.values.frame.righteye.psize);
        hashMap.put(Message.TIMESTAMP, this.values.frame);
        hashMap.put(Message.CATEGORY, category);
        hashMap.put(Message.REQUEST, request);
        hashMap.put(Message.STATE, this.values.frame.state);
        hashMap.put(Message.STATUSCODE, statuscode);
        return hashMap;
    }
}
