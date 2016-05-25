package model;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;

/**
 * @Author Dmitry Volovod
 * created on 25.05.2016
 */
// wrapper class
public class MissingValues {
    ArrayList<Pair<Integer, String>> series;

    public String toString() {
        String s = "Size: " + series.size();
        for (Pair<Integer, String> sery : series) {
            s += " " + sery.toString();
        }
        return s;
    }
    public MissingValues() {
        series = new ArrayList<>();
    }

    // Pair<Integer, String>: Integer - number of trackers message, String - kind of miss
    public boolean add(Pair<Integer, String> item) {
        return series.add(item);
    }

    public Pair<Integer, String> get(int i) {
        return series.get(i);
    }

    public ArrayList<Pair<Integer, String>> getSeries() {
        return series;
    }

    public int size() {
        return series.size();
    }
}
