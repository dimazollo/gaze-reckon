package statistics;

import model.MappedDataItem;
import model.test.Stimulus;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Dmitry Volovod
 * created on 09.04.2016
 */
public class Utils {

    public static Double[] optimizeTwoParameters(Double mu, Double sigma, List<Double> doubles){
        double step = 0.01;
        Double currentSigma = null;
        Double currentMu = null;
        Double chiSquared;
        double trial;
        double maxSigma = 2 * sigma;
        double minSigma = 0;
        double maxMu = mu + sigma;
        double minMu = mu - sigma;
        boolean muIsGreater, sigmaIsGreater;
        while (maxSigma - minSigma > step) {
            currentSigma = (maxSigma - minSigma) / 2 + minSigma;
            currentMu = (maxMu - minMu) / 2 + minMu;
            chiSquared = Stats.chiSquared(currentMu, currentSigma, doubles);
            trial = Stats.chiSquared(currentMu, currentSigma + step, doubles);
            if (trial < chiSquared) {
                sigmaIsGreater = true;
            } else {
                sigmaIsGreater = false;
            }
            trial = Stats.chiSquared(currentMu + step, currentSigma, doubles);
            if (trial < chiSquared) {
                muIsGreater = true;
            } else {
                muIsGreater = false;
            }
            if (sigmaIsGreater) {
                minSigma = currentSigma;
            } else {
                maxSigma = currentSigma;
            }
            if (muIsGreater) {
                minMu = currentMu;
            } else {
                maxMu = currentMu;
            }
        }
        return new Double[]{currentMu, currentSigma};
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

    public static Integer[] countSampleHits(List<Double> doubles, Double numberOfIntervals) {
        Double minValue = Stats.min(doubles);
        Double maxValue = Stats.max(doubles);
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
//            if (i != numberOfIntervals.intValue() - 1) {
//                System.out.println(i + " : " + r_i);
//            } else {
//                System.out.println(i + " : " + (r_i + 1));
//            }
        }
        resultArray[resultArray.length - 1]++;  //прибавление последнего максимального значения
        return resultArray;
    }
}
