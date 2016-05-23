package dataRecovery;

import org.apache.commons.math3.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Dmitry Volovod
 * created on 24.05.2016
 */
public final class Regression {
    private static final double THRESHOLD = 0.1;

    private static double gaussianKernel(double r) {
        return Math.exp(-2.0 * r * r);
    }

    private static double calculateWeight(double distance, double h) {
        return gaussianKernel(distance / h);
    }

    // Непараметрическая регрессия Надарая-Ватсона
    public static List<Pair<Long, Double>> nadarayaWatson(List<Pair<Long, Double>> pairs, double h, boolean onlyMisses) {
        LinkedList<Pair<Long, Double>> resultList = new LinkedList<>();
        Double numerator, denominator;
        Double weight, numeratorTerm;
        for (int i = 0; i < pairs.size(); i++) {
            if (onlyMisses && pairs.get(i).getValue() == 0.0) continue;
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
}
