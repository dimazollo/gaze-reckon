package dataRecovery;

import model.eyetracker.Frame;
import model.eyetracker.Message;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dmitry Volovod
 * created on 16.11.2015
 */

public final class DataRecovery {
    private static final double THRESHOLD = 0.1;

    // Method of checking time field in the array of tracker messages and do all values unique.
    public static int correctRepetitive(ArrayList<Message> messages) {
        if (messages.size() < 2) return 0;
        int counter = 0;
        for (int i = 1; i < messages.size(); i++) {
            if (messages.get(i).values.frame.time ==
                    messages.get(i - 1).values.frame.time) {
                messages.get(i).values.frame.time += 20; // Make time value be unique.
                // TODO-Dmitry: Here should be code that makes timestamp be unique too.
                counter++;
                // System.out.println(counter + " " + trackers.get(i-1).values.frame.time);
            }
        }
        return counter;
    }

    private static ArrayList<MissingValues> findMisses(ArrayList<Message> messages) {
        MissingValues series;
        ArrayList<MissingValues> allMissingSeries = new ArrayList<>();
        // Searching for a series of missing values.
        int i = 0;
        while (i < messages.size()) {
            series = new MissingValues();
            String kindOfMiss;
            do {
                Message message = messages.get(i);
                kindOfMiss = message.hasMissingData();
                i++;
            } while (kindOfMiss == null && i < messages.size());
            i--;
            do {
                Message message = messages.get(i);
                kindOfMiss = message.hasMissingData();
                if (kindOfMiss != null) {
                    Pair<Integer, String> item = new Pair<>(i, kindOfMiss);
                    series.add(item);
                }
                i++;
            } while (kindOfMiss != null && i < messages.size());
            allMissingSeries.add(series);
        }
        return allMissingSeries;
    }

    // Here are bunch of data recovery approaches. they should be coded separately.
    private static void interpolate(ArrayList<Message> messages, MissingValues series) {
        if (series.size() < 4 && series.size() > 0) {
            if (series.get(0).getKey() != 0 && series.get(series.size() - 1).getKey() != 0) {
                Message first = messages.get(series.get(0).getKey() - 1);
                Message last = messages.get(series.get(series.size() - 1).getKey() + 1);
                for (int j = 0; j < series.size(); j++) {
                    Message current = messages.get(series.get(j).getKey());
                    // TODO: To reduce amount of code it is better to redefine arithmetical operators in Frame.
                    current.values.frame.raw.x = (j + 1) * (last.values.frame.raw.x
                            - first.values.frame.raw.x) / (series.size() + 1)
                            + first.values.frame.raw.x;
                    current.values.frame.raw.y = (j + 1) * (last.values.frame.raw.y
                            - first.values.frame.raw.y) / (series.size() + 1)
                            + first.values.frame.raw.y;
                    current.values.frame.avg.x = (j + 1) * (last.values.frame.avg.x
                            - first.values.frame.avg.x) / (series.size() + 1)
                            + first.values.frame.avg.x;
                    current.values.frame.avg.y = (j + 1) * (last.values.frame.avg.y
                            - first.values.frame.avg.y) / (series.size() + 1)
                            + first.values.frame.avg.y;

                    if (series.get(j).getValue().equals(Frame.LEFT) || series.get(j).getValue().equals(Frame.BOTH)) {
                        current.values.frame.lefteye.avg.x = (j + 1) * (last.values.frame.lefteye.avg.x
                                - first.values.frame.lefteye.avg.x) / (series.size() + 1)
                                + first.values.frame.lefteye.avg.x;
                        current.values.frame.lefteye.avg.y = (j + 1) * (last.values.frame.lefteye.avg.y
                                - first.values.frame.lefteye.avg.y) / (series.size() + 1)
                                + first.values.frame.lefteye.avg.y;
                        current.values.frame.lefteye.raw.x = (j + 1) * (last.values.frame.lefteye.raw.x
                                - first.values.frame.lefteye.raw.x) / (series.size() + 1)
                                + first.values.frame.lefteye.raw.x;
                        current.values.frame.lefteye.raw.y = (j + 1) * (last.values.frame.lefteye.raw.y
                                - first.values.frame.lefteye.raw.y) / (series.size() + 1)
                                + first.values.frame.lefteye.raw.y;
                    }
                    if (series.get(j).getValue().equals(Frame.RIGHT) || series.get(j).getValue().equals(Frame.BOTH)) {
                        current.values.frame.righteye.avg.x = (j + 1) * (last.values.frame.righteye.avg.x
                                - first.values.frame.righteye.avg.x) / (series.size() + 1)
                                + first.values.frame.righteye.avg.x;
                        current.values.frame.righteye.avg.y = (j + 1) * (last.values.frame.righteye.avg.y
                                - first.values.frame.righteye.avg.y) / (series.size() + 1)
                                + first.values.frame.righteye.avg.y;
                        current.values.frame.righteye.raw.x = (j + 1) * (last.values.frame.righteye.raw.x
                                - first.values.frame.righteye.raw.x) / (series.size() + 1)
                                + first.values.frame.righteye.raw.x;
                        current.values.frame.righteye.raw.y = (j + 1) * (last.values.frame.righteye.raw.y
                                - first.values.frame.righteye.raw.y) / (series.size() + 1)
                                + first.values.frame.righteye.raw.y;
                    }
                }
            }
        }
    }

    public static void interpolationRecovery(ArrayList<Message> messages) {
        ArrayList<MissingValues> missingValues = DataRecovery.findMisses(messages);
        for (MissingValues missingValue : missingValues) {
            interpolate(messages, missingValue);
        }
    }

    public static void oneEyeMissRecovery(ArrayList<Message> messages) {
        messages.forEach(DataRecovery::oneEyeMissRecovery);
    }

    @Deprecated
    public static void listwiseDeletionUnsafe(ArrayList<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).hasMissingData() != null) {
                messages.remove(i);
                i--;
            }
        }
    }

    public static ArrayList<Message> listwiseDeletion(ArrayList<Message> messages) {
        List<Message> result = messages.stream().filter(
                message -> message.hasMissingData() == null).collect(Collectors.toCollection(LinkedList::new)
        );
        return new ArrayList<>(result);
    }

    private static double gaussianKernel(double r) {
        return Math.exp(-2.0 * r * r);
    }

    private static double calculateWeight(double distance, double h) {
        return gaussianKernel(distance / h);
    }

    // Непараметрическая регрессия Надарая-Ватсона
    public static List<Pair<Long, Double>> kernelRegression(List<Pair<Long, Double>> pairs, double h) {
        LinkedList<Pair<Long, Double>> resultList = new LinkedList<>();
        Double numerator, denominator;
        Double weight, numeratorTerm;
        for (int i = 0; i < pairs.size(); i++) {
            int j = i - 1;
            numerator = 0.0;
            denominator = 0.0;
            while (j >= 0) {
                if (pairs.get(j).getValue() != 0.0) {
                    weight = calculateWeight(pairs.get(j).getKey() - pairs.get(i).getKey(), h);
//                    weight = calculateWeight(Point.distance(pairs.get(j).getKey(), pairs.get(j).getValue(),
//                            pairs.get(i).getKey(), pairs.get(i).getValue()), h);
                } else {
                    j--;
                    continue;
                }
                numeratorTerm = pairs.get(j).getValue() * weight;
                numerator += numeratorTerm;
                denominator += weight;
                if (numeratorTerm / denominator < THRESHOLD) break;
                j--;
            }
            j = i;
            while (j < pairs.size()) {
                if (pairs.get(j).getValue() != 0.0) {
                    weight = calculateWeight(pairs.get(j).getKey() - pairs.get(i).getKey(), h);
//                    weight = calculateWeight(Point.distance(pairs.get(j).getKey(), pairs.get(j).getValue(),
//                                    pairs.get(i).getKey(), pairs.get(i).getValue()), h);
                } else {
                    j++;
                    continue;
                }
                numeratorTerm = pairs.get(j).getValue() * weight;
                numerator += numeratorTerm;
                denominator += weight;
                if (numeratorTerm / denominator < THRESHOLD) break;
                j++;
            }
            resultList.add(new Pair<>(pairs.get(i).getKey(), numerator / denominator));
        }
        return resultList;
    }

    // method, that copies registered value of one eye to missing value of another
    private static void oneEyeMissRecovery(Message message) {
        String kindOfMiss = message.hasMissingData();
        if (kindOfMiss != null) {
            if (kindOfMiss.equals(Frame.RIGHT)) {
                message.values.frame.righteye = message.values.frame.lefteye;
            } else if (kindOfMiss.equals(Frame.LEFT)) {
                message.values.frame.lefteye = message.values.frame.righteye;
            }
        }
    }

    // wrapper class
    private static class MissingValues {
        static ArrayList<Pair<Integer, String>> series;

        MissingValues() {
            series = new ArrayList<>();
        }

        // Pair<Integer, String>: Integer - number of trackers message, String - kind of miss
        boolean add(Pair<Integer, String> item) {
            return series.add(item);
        }

        Pair<Integer, String> get(int i) {
            return series.get(i);
        }

        int size() {
            return series.size();
        }
    }
}