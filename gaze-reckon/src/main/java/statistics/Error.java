package statistics;

import model.MappedDataItem;
import model.eyetracker.Message;
import model.test.Stimulus;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.util.*;

/**
 * @Author Dmitry Volovod
 * created on 10.03.2016
 */
public final class Error {
    final static double ALPHA = 0.05;   // Коэффициент значимости. Доверительная вероятность P = 1 - ALPHA.

    public static HashMap<Stimulus[], Boolean> sigelTukeyTest(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = Utils.getUniqueStimuli(mappedDataList);
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }
        HashMap<Stimulus[], Boolean> results = new HashMap<>();
        HashMap<Stimulus, ArrayList<Double>> xMap = new HashMap<>();
        HashMap<Stimulus, ArrayList<Double>> yMap = new HashMap<>();
        for (Stimulus currentStimulus : stimuli) {
            List xList = new LinkedList<>();
            List yList = new LinkedList<>();
            for (MappedDataItem mappedDataItem : filteredMappedData) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    for (Message message : mappedDataItem.getFixationsMessages()) {
                        xList.add(message.values.frame.raw.x);
                        yList.add(message.values.frame.raw.y);
                    }
                }
            }
            xMap.put(currentStimulus, new ArrayList<>(xList));
            yMap.put(currentStimulus, new ArrayList<>(yList));
        }
        ArrayList<Boolean[]> bArray = new ArrayList<>();
        for (Map.Entry<Stimulus, ArrayList<Double>> entry1 : xMap.entrySet()) {
            for (Map.Entry<Stimulus, ArrayList<Double>> entry2 : xMap.entrySet()) {
                Boolean fTest = Stats.sigelTukeyTest(entry1.getValue(), entry2.getValue());
                bArray.add(new Boolean[]{fTest, null});
            }
        }
        Iterator<Boolean[]> iterator = bArray.iterator();
        for (Map.Entry<Stimulus, ArrayList<Double>> entry1 : yMap.entrySet()) {
            for (Map.Entry<Stimulus, ArrayList<Double>> entry2 : yMap.entrySet()) {
                Boolean fTest = Stats.sigelTukeyTest(entry1.getValue(), entry2.getValue());
                Boolean[] t = iterator.next();
                t[1] = fTest;
                results.put(new Stimulus[]{entry1.getKey(), entry2.getKey()}, t[0] && t[1]);
            }
        }
        return results;
    }

    public static HashMap<Stimulus, Boolean> normalityTestForDeviations(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = Utils.getUniqueStimuli(mappedDataList);
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }
        HashMap<Stimulus, Boolean> results = new HashMap<>();
        List<Double> dList;
        Double meanD;
        for (Stimulus currentStimulus : stimuli) {
            dList = new LinkedList<>();
            meanD = 0.0;
            for (MappedDataItem mappedDataItem : filteredMappedData) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    List<Message> messages = mappedDataItem.getFixationsMessages();
                    for (Message message : messages) {
                        Double d = message.values.frame.raw.getDistance(currentStimulus.getPosition());
                        dList.add(d);
                        meanD += d;
                    }
                }
            }
            //if (dList.isEmpty() || yList.isEmpty()) continue;
            meanD /= dList.size();
            System.out.println("meanD = " + meanD + ", X = " + currentStimulus.getPosition().x);
            List<Double> deviationsD = Stats.calculateDeviations(dList, meanD);
            Double standardDeviationD = Stats.standardDeviation(deviationsD);
            System.out.println("stdD = " + standardDeviationD);
            Boolean chiTestD = null;
            if (!dList.isEmpty()) {
                ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(Stats.sturgesRule(dList.size()));
                Double chiTest = chiSquaredDistribution.inverseCumulativeProbability(ALPHA);
                System.out.println("expected chi = " + chiTest);
                Double chiD = Stats.chiSquared(meanD, standardDeviationD, dList);
                System.out.println("chi D = " + chiD);
                System.out.println();
                chiTestD = chiD <= chiTest;
            }
            results.put(currentStimulus, chiTestD);
        }
        return results;
    }

    // Проверка на нормальноссть распределения точек взора вокруг выборочного математического ожидания.
    public static HashMap<Stimulus, Boolean[]> normalityTest(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = Utils.getUniqueStimuli(mappedDataList);
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }
        HashMap<Stimulus, Boolean[]> results = new HashMap<>();
        List<Double> xList, yList;
        Double meanX, meanY;
        for (Stimulus currentStimulus : stimuli) {
            xList = new LinkedList<>();
            yList = new LinkedList<>();
            meanX = 0.0;
            meanY = 0.0;
            for (MappedDataItem mappedDataItem : filteredMappedData) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    List<Message> messages = mappedDataItem.getFixationsMessages();
                    for (Message message : messages) {
                        xList.add(message.values.frame.raw.x);
                        yList.add(message.values.frame.raw.y);
                        meanX += message.values.frame.raw.x;
                        meanY += message.values.frame.raw.y;
                    }
                }
            }
            //if (dList.isEmpty() || yList.isEmpty()) continue;
            meanX /= xList.size();
            System.out.println("meanD = " + meanX + ", X = " + currentStimulus.getPosition().x);
            meanY /= yList.size();
            System.out.println("meanY = " + meanY + ", Y = " + currentStimulus.getPosition().y);

            List<Double> deviationsX = Stats.calculateDeviations(xList, meanX);
            List<Double> deviationsY = Stats.calculateDeviations(yList, meanY);

            Double standardDeviationX = Stats.standardDeviation(deviationsX);
            System.out.println("stdX = " + standardDeviationX);
            Double standardDeviationY = Stats.standardDeviation(deviationsY);
            System.out.println("stdY = " + standardDeviationY);
            Boolean chiTestX = null, chiTestY = null;
            if (!xList.isEmpty()) {
                ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(Stats.sturgesRule(xList.size()));
                Double chiTest = chiSquaredDistribution.inverseCumulativeProbability(ALPHA);

                Double[] resX = Utils.optimizeUniParameter(meanX, standardDeviationX, xList);
                meanX = resX[0];
                standardDeviationX = resX[1];
                Double[] resY = Utils.optimizeUniParameter(meanY, standardDeviationY, yList);
                meanY = resY[0];
                standardDeviationY = resY[1];
                System.out.println("expected chi = " + chiTest);
                Double chiX = Stats.chiSquared(meanX, standardDeviationX, xList);
                System.out.println("chi X = " + chiX);
                Double chiY = Stats.chiSquared(meanY, standardDeviationY, yList);
                System.out.println("chi Y = " + chiY);
                System.out.println();
                chiTestX = chiX <= chiTest;
                chiTestY = chiY <= chiTest;
            }
            results.put(currentStimulus, new Boolean[]{chiTestX, chiTestY});
        }
        return results;
    }

    // Подсчёт пропусков в местах предъявленных стимулов.
    // Одинаковыми считаются стимулы, предъявленные в разное время, но в одинаковых координатах.
    public static HashMap<Stimulus, Integer> countMissesByStimulus(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = Utils.getUniqueStimuli(mappedDataList);
        HashMap<Stimulus, Integer> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuli) {
            Integer counter = 0;
            for (MappedDataItem mappedDataItem : mappedDataList) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    for (Message message : mappedDataItem.getMessages()) {
                        if (message.hasMissingData() != null) counter++;
                    }
                }
            }
            results.put(currentStimulus, counter);
        }
        return results;
    }


    // На базе MappedData
    public static HashMap<Stimulus, Double[]> computeDeviations(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = Utils.getUniqueStimuli(mappedDataList);

        // Отфильтровавыние стимулов, на которых алгоритм выделения фиксации среди саккад и латентного периода не нашел этой самой фиксации.
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }

        // Фильтрация "некомплектных" стимулов.
//        for (MappedDataItem mappedDataItem : mappedDataList) {
//            if(!mappedDataItem.hasMissingData()) {
//                mappedDataItem.computeStatistics();
//                filteredMappedData.add(mappedDataItem);
//            }
//        }

        List<Double> deltasX;
        List<Double> deltasY;
        Double minDeltaX;
        Double maxDeltaX;
        Double minDeltaY;
        Double maxDeltaY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuli) {
            deltasX = new LinkedList<>();
            deltasY = new LinkedList<>();
            minDeltaX = null;
            maxDeltaX = null;
            minDeltaY = null;
            maxDeltaY = null;
            for (MappedDataItem mappedDataItem : filteredMappedData) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    if (minDeltaX == null || maxDeltaX == null || minDeltaY == null || maxDeltaY == null) { // Redundant clause. Can be simplified.
                        minDeltaX = mappedDataItem.getMinDeltaX();
                        maxDeltaX = mappedDataItem.getMaxDeltaX();
                        minDeltaY = mappedDataItem.getMinDeltaY();
                        maxDeltaY = mappedDataItem.getMaxDeltaY();
                    } else {
                        if (minDeltaX > mappedDataItem.getMinDeltaX()) minDeltaX = mappedDataItem.getMinDeltaX();
                        if (maxDeltaX < mappedDataItem.getMaxDeltaX()) maxDeltaX = mappedDataItem.getMaxDeltaX();
                        if (minDeltaY > mappedDataItem.getMinDeltaY()) minDeltaY = mappedDataItem.getMinDeltaY();
                        if (maxDeltaY < mappedDataItem.getMaxDeltaY()) maxDeltaY = mappedDataItem.getMaxDeltaY();
                    }
                    deltasX.add(mappedDataItem.getMeanX() - currentStimulus.getPosition().x);
                    deltasY.add(mappedDataItem.getMeanY() - currentStimulus.getPosition().y);
                }
                Double absDevX = Stats.absoluteDeviation(deltasX);
                Double absDevY = Stats.absoluteDeviation(deltasY);
                Double stDevX = Stats.standardDeviation(deltasX);
                Double stDevY = Stats.standardDeviation(deltasY);
                results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY, minDeltaX, maxDeltaX, minDeltaY, maxDeltaY});
            }
        }
        return results;
    }

    // Отличается от предыдущего тем, что стимулы считаются разными, если предъявлены в одинаковых координатах, но в разное время.
    public static HashMap<Stimulus, Double[]> computeDeviationsForEach(LinkedHashMap<Message, Stimulus> filteredMap) {
        HashSet<Stimulus> stimuliSet = new HashSet<>();
        boolean contains;
        for (Stimulus stimulus : filteredMap.values()) { // Пробегаем по всем стимулам и добавляем каждый предъявленный стимул.
            Stimulus tmpStimulus = new Stimulus(stimulus.getPosition().x, stimulus.getPosition().y, stimulus.getTimestamp());
            contains = false;
            for (Stimulus s : stimuliSet) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) stimuliSet.add(tmpStimulus);
        }
        // Часть с рачётом отклонений.
        ArrayList<Double> deltasX;
        ArrayList<Double> deltasY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuliSet) {
            deltasX = new ArrayList<>();
            deltasY = new ArrayList<>();
            // Одинаковыми считаются стимулы с одинаковыми координатами.
            for (Map.Entry<Message, Stimulus> entry : filteredMap.entrySet()) {
                if (currentStimulus.getPosition().x == entry.getValue().getPosition().x && currentStimulus.getPosition().y == entry.getValue().getPosition().y) {
                    deltasX.add(entry.getKey().values.frame.raw.x - entry.getValue().getPosition().x);
                    deltasY.add(entry.getKey().values.frame.raw.y - entry.getValue().getPosition().y);
                }
            }
            Double absDevX = Stats.absoluteDeviation(deltasX);
            Double absDevY = Stats.absoluteDeviation(deltasY);
            Double stDevX = Stats.standardDeviation(deltasX);
            Double stDevY = Stats.standardDeviation(deltasY);
            results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY});
        }
        return results;
    }

    public static Double[] computeDeviationsForAll(ArrayList<MappedDataItem> mappedData) {
        // Часть с рачётом отклонений.
        LinkedList<Double> deltasX = new LinkedList<>();
        LinkedList<Double> deltasY = new LinkedList<>();
        Integer numberOfMappedMsgs = 0;
        Integer numberOfFixMsgs = 0;
        for (MappedDataItem mappedDataItem : mappedData) {
            numberOfMappedMsgs += mappedDataItem.getMessages().size();
            numberOfFixMsgs += mappedDataItem.getFixationsMessages().size();
            for (Message message : mappedDataItem.getFixationsMessages()) {
                deltasX.add(message.values.frame.raw.x - mappedDataItem.getStimulus().getPosition().x);
                deltasY.add(message.values.frame.raw.y - mappedDataItem.getStimulus().getPosition().y);
            }
        }

        Double absDevX = Stats.absoluteDeviation(deltasX);
        Double absDevY = Stats.absoluteDeviation(deltasY);
        Double stDevX = Stats.standardDeviation(deltasX);
        Double stDevY = Stats.standardDeviation(deltasY);

        System.out.println("Mapped messages = " + numberOfMappedMsgs);
        System.out.println("Fixation messages = " + numberOfFixMsgs);
        Double[] results = new Double[]{absDevX, absDevY, stDevX, stDevY};
        return results;
    }
}