package statistics;

import model.MappedDataItem;
import model.eyetracker.Message;
import model.test.Stimulus;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.*;

//import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * @Author Dmitry Volovod
 * created on 10.03.2016
 */
public final class Error {
    private final static String LAG_PHASE = "lag phase";
    private final static String SACCADE = "saccade";
    private final static String FIXATION = "fixation";

    private final static double ALPHA = 0.1;   // Коэффициент значимости. Доверительная вероятность P = 1 - ALPHA.

    // Вариант под класс MappedData.
    //TODO - Метод стоит подумать об удалении метода, т.к. он переехал в MappedDataItem
    @Deprecated
    static ArrayList<MappedDataItem> filterMappedData(ArrayList<MappedDataItem> mappedDataArrayList) {
        ArrayList<MappedDataItem> filteredMappedDataList = new ArrayList<>();
        for (MappedDataItem mappedDataItem : mappedDataArrayList) {
            String status = LAG_PHASE;
            ArrayList<Message> messages = mappedDataItem.getMessages();
            ArrayList<Message> filteredMessages = new ArrayList<>();
            MappedDataItem filteredMappedDataItem;
            for (Message message : messages) {
                // Выделение периода фиксации, удаляя саккады и латентный период.
                switch (status) {
                    case LAG_PHASE:
                        if (!message.values.frame.fix) {
                            status = SACCADE;
                        }
                        break;

                    case SACCADE:
                        if (message.values.frame.fix) {
                            status = FIXATION;
                            filteredMessages.add(message);
                        }
                        break;

                    case FIXATION:
                        if (message.values.frame.fix) {  // Добавляем только точки, в которых была фиксация.
                            filteredMessages.add(message);
                        }
                        break;
                }
            }
            filteredMappedDataItem = new MappedDataItem(mappedDataItem.getStimulus(), filteredMessages);
            filteredMappedDataList.add(filteredMappedDataItem);
        }
        return filteredMappedDataList;
    }

    // Вариант под LinkedHashMap.
    @Deprecated
    public static LinkedHashMap<Message, Stimulus> filterStimulusMessageMap(HashMap<Message, Stimulus> messageStimulusMap) {
        LinkedHashMap<Message, Stimulus> filteredMap = new LinkedHashMap<>();
        String status = LAG_PHASE;
        Stimulus currentStimulus = messageStimulusMap.entrySet().iterator().next().getValue();  // Первый стимул.
        for (Map.Entry<Message, Stimulus> entry : messageStimulusMap.entrySet()) {
            switch (status) {
                case LAG_PHASE:
                    if (!entry.getKey().values.frame.fix) {
                        status = SACCADE;
                    }
                    break;

                case SACCADE:
                    if (entry.getKey().values.frame.fix) {
                        status = FIXATION;
                        filteredMap.put(entry.getKey(), entry.getValue());
                    }
                    break;

                case FIXATION:
                    if (entry.getValue().equals(currentStimulus)) { // Если текущий стимул не равен предыдущему, то начало латентного периода.
                        if (entry.getKey().values.frame.fix) {  // Добавляем только точки, в которых была фиксация.
                            filteredMap.put(entry.getKey(), entry.getValue());
                        }
                    } else {
                        status = LAG_PHASE;
                        currentStimulus = entry.getValue();
                    }
                    break;
            }
        }
        return filteredMap;
    }

    // Возвращает список стимулов с уникальными координатами.
    public static List<Stimulus> getUniqueStimuli(List<MappedDataItem> mappedDataList) {
        List<Stimulus> uniqueStimuli = new LinkedList<>();
        boolean contains;
        for (MappedDataItem mappedDataItem : mappedDataList) { // Пробегаем по всем стимулам и добавляем только одинаковые.
            Stimulus tmpStimulus = new Stimulus(mappedDataItem.getStimulus().getPosition().x, mappedDataItem.getStimulus().getPosition().y, null);
            contains = false;
            for (Stimulus s : uniqueStimuli) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) uniqueStimuli.add(tmpStimulus);
        }
        return uniqueStimuli;
    }

    public static Double min(List<Double> array) {
        Double min = array.get(0);
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) < min) min = array.get(i);
        }
        return min;
    }

    public static Double max(List<Double> array) {
        Double max = array.get(0);
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) > max) max = array.get(i);
        }
        return max;
    }

    public static Double chiSquared(Double mu, Double sigma, List<Double> doubles) {
        int n = doubles.size(); //число проведенных экспериментов
        Double numberOfIntervals = sturgesRule(doubles.size());
        //расчёт попаданий в интервалы
        Integer[] sampleHits = countSampleHits(doubles, numberOfIntervals);
        //расчёт теоретических вероятностей попадения в интервалы
        Double[] probability = new Double[numberOfIntervals.intValue()];
        Double maxValue = max(doubles);
        Double minValue = min(doubles);
        Double step = (maxValue - minValue) / numberOfIntervals;
        NormalDistribution normalDistribution = new NormalDistribution(mu, sigma); //mu - mean, sigma - standard deviation
        for (int i = 0; i < numberOfIntervals; i++) {
            if (i == 0) probability[i] = normalDistribution.cumulativeProbability(minValue + step * (i + 1));
            else if (i == numberOfIntervals - 1)
                probability[i] = 1 - normalDistribution.cumulativeProbability(step * i + minValue);
            else
                probability[i] = normalDistribution.cumulativeProbability(step * (i + 1) + minValue) - normalDistribution.cumulativeProbability(step * i + minValue);
        }
//        Double[] probability = theoreticDistribution(distributionLaw, parameters, model);
//        for (Double aDouble : probability) {
//            System.out.println(aDouble);
//        }
        // Вычисление хи квадрат
//        System.out.println("Theoretical intervals:");
        Double result = 0.0;
        for (int i = 0; i < numberOfIntervals; i++) {
//            System.out.println(i + " : " + n * probability[i]);
            result += Math.pow(sampleHits[i] - n * probability[i], 2.0) / (n * probability[i]);
        }
        return result;
    }

    public static HashMap<Stimulus, Boolean> normalityTestForDeviations(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = getUniqueStimuli(mappedDataList);
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }
        HashMap<Stimulus, Boolean> results = new HashMap<>();
        List<Double> dList, yList;
        Double meanD, meanY;
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
            List<Double> deviationsD = calculateDeviations(dList, meanD);
            Double standardDeviationD = standardDeviation(deviationsD);
            System.out.println("stdD = " + standardDeviationD);
            Boolean chiTestD = null;
            if (!dList.isEmpty()) {
                ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(sturgesRule(dList.size()));
                Double chiTest = chiSquaredDistribution.inverseCumulativeProbability(ALPHA);
                System.out.println("expected chi = " + chiTest);
                Double chiD = chiSquared(meanD, standardDeviationD, dList);
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
        List<Stimulus> stimuli = getUniqueStimuli(mappedDataList);
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

            List<Double> deviationsX = calculateDeviations(xList, meanX);
            List<Double> deviationsY = calculateDeviations(yList, meanY);

            Double standardDeviationX = standardDeviation(deviationsX);
            System.out.println("stdX = " + standardDeviationX);
            Double standardDeviationY = standardDeviation(deviationsY);
            System.out.println("stdY = " + standardDeviationY);
            Boolean chiTestX = null, chiTestY = null;
            if (!xList.isEmpty()) {
                ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(sturgesRule(xList.size()));
                Double chiTest = chiSquaredDistribution.inverseCumulativeProbability(ALPHA);
                System.out.println("expected chi = " + chiTest);
                Double chiX = chiSquared(meanX, standardDeviationX, xList);
                System.out.println("chi X = " + chiX);
                Double chiY = chiSquared(meanY, standardDeviationY, yList);
                System.out.println("chi Y = " + chiY);
                System.out.println();
                chiTestX = chiX <= chiTest;
                chiTestY = chiY <= chiTest;
            }
            results.put(currentStimulus, new Boolean[]{chiTestX, chiTestY});
        }
        return results;
    }

    private static List<Double> calculateDeviations(List<Double> doubles, Double mean) {
        List<Double> result = new LinkedList<>();
        for (Double aDouble : doubles) {
            result.add(aDouble - mean);
        }
        return result;
    }

    public static Double sturgesRule(Integer numberOfExperiments) {
        return Math.floor(1 + 3.3 * Math.log10((double) numberOfExperiments));
        //return 15.0;
    }

    public static Integer[] countSampleHits(List<Double> doubles, Double numberOfIntervals) {
        Double minValue = min(doubles);
        Double maxValue = max(doubles);
        Double timeInterval = (maxValue - minValue) / numberOfIntervals;
        Integer[] resultArray = new Integer[numberOfIntervals.intValue()];
        for (int i = 0; i < numberOfIntervals.intValue(); i++) {
            int r_i = 0; //число попаданий в i-й интервал
            for (int j = 0; j < doubles.size(); j++) {
                if (minValue + (timeInterval * i) <= doubles.get(j) &&
                        doubles.get(j) < minValue + timeInterval * (i + 1)) {
                    r_i++;
                }
            }
            resultArray[i] = r_i;
            if (i != numberOfIntervals.intValue() - 1){
                System.out.println(i + " : " + r_i);
            } else {
                System.out.println(i + " : " + (r_i + 1));
            }
        }
        resultArray[resultArray.length - 1]++;  //прибавление последнего максимального значения
        return resultArray;
    }


    // На базе MappedData
    public static HashMap<Stimulus, Double[]> computeDeviations(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = getUniqueStimuli(mappedDataList);

        // Отфильтровавыние стимулов, на которых алгоритм выделения фиксации среди саккад и латентного периода не нашел этой самой фиксации.
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if (mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }

        // Фильрация "некомплектных" стимулов.
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
                Double absDevX = absoluteDeviation(deltasX);
                Double absDevY = absoluteDeviation(deltasY);
                Double stDevX = standardDeviation(deltasX);
                Double stDevY = standardDeviation(deltasY);
                results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY, minDeltaX, maxDeltaX, minDeltaY, maxDeltaY});
            }
        }
        return results;
    }

    // На базе смапленных хэшкарт.
    public static HashMap<Stimulus, Double[]> computeDeviations(LinkedHashMap<Message, Stimulus> filteredMap) {
        HashSet<Stimulus> stimuliSet = new HashSet<>();
        boolean contains;
        for (Stimulus stimulus : filteredMap.values()) { // Пробегаем по всем стимулам и добавляем только одинаковые.
            Stimulus tmpStimulus = new Stimulus(stimulus.getPosition().x, stimulus.getPosition().y, null);
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
            Double absDevX = absoluteDeviation(deltasX);
            Double absDevY = absoluteDeviation(deltasY);
            Double stDevX = standardDeviation(deltasX);
            Double stDevY = standardDeviation(deltasY);
            results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY});
        }
        return results;
    }

    // Отличается от предыдущего тем, что стимулы считаются разными, если предъявлены в одинаковых координатах, но в разное время.
    public static HashMap<Stimulus, Double[]> computeDeviationsForEach
    (LinkedHashMap<Message, Stimulus> filteredMap) {
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
            Double absDevX = absoluteDeviation(deltasX);
            Double absDevY = absoluteDeviation(deltasY);
            Double stDevX = standardDeviation(deltasX);
            Double stDevY = standardDeviation(deltasY);
            results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY});
        }
        return results;
    }

    // Расчёт среднеквадратичного отклонения.

    public static Double standardDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += Math.pow(deviation, 2.0);
        }
        return Math.sqrt(sum / deviations.size());
    }

    // Расчёт абсолютного отклонения.
    public static Double absoluteDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += deviation;
        }
        return sum / deviations.size();
    }
}