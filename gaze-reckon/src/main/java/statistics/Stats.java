package statistics;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Dmitry Volovod
 * created on 09.04.2016
 */
public class Stats {

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
        Integer[] sampleHits = Utils.countSampleHits(doubles, numberOfIntervals);
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
        // Вычисление хи квадрат
//        System.out.println("Theoretical intervals:");
        Double result = 0.0;
        for (int i = 0; i < numberOfIntervals; i++) {
//            System.out.println(i + " : " + n * probability[i]);
            result += Math.pow(sampleHits[i] - n * probability[i], 2.0) / (n * probability[i]);
        }
        return result;
    }

    static Double mean(List<Double> sample) {
        Double result = 0.0;
        for (Double aDouble : sample) {
            result += aDouble;
        }
        result /= sample.size();
        return result;
    }

    public static Double sturgesRule(Integer numberOfExperiments) {
        return Math.floor(1 + 3.3 * Math.log10((double) numberOfExperiments));
        //return 15.0;
    }

    public static Double standardDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += Math.pow(deviation, 2.0);
        }
        return Math.sqrt(sum / (deviations.size() - 1));
    }

    // Расчёт абсолютного отклонения.
    public static Double absoluteDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += deviation;
        }
        return sum / deviations.size();
    }

    static Boolean fisherTest(List<Double> sample1, List<Double> sample2) {
//        if (sample1.equals(sample2)) {
//            return true;
//        }
        Double m1 = mean(sample1);
        List<Double> d1List = calculateDeviations(sample1, m1);
        Double d1 = standardDeviation(d1List);
        Double m2 = mean(sample2);
        List<Double> d2List = calculateDeviations(sample2, m2);
        Double d2 = standardDeviation(d2List);
        Double t;
        FDistribution fDistribution;
        if (!sample1.isEmpty() && !sample2.isEmpty()) {
            if (d1 > d2) {
                fDistribution = new FDistribution(sample1.size() - 1, sample2.size() - 1);
                t = Math.pow(d1, 2.0) / Math.pow(d2, 2.0);
            } else {
                fDistribution = new FDistribution(sample2.size() - 1, sample1.size() - 1);
                t = Math.pow(d2, 2.0) / Math.pow(d1, 2.0);
            }
            double criticalValue = fDistribution.inverseCumulativeProbability(1 - Error.ALPHA);
            return t < criticalValue;
        } else return null;
    }

    static List<Double> calculateDeviations(List<Double> doubles, Double mean) {
        List<Double> result = new LinkedList<>();
        for (Double aDouble : doubles) {
            result.add(aDouble - mean);
        }
        return result;
    }
}
