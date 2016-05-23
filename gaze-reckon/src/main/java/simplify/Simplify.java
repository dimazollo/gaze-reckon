package simplify;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @Author Dmitry Volovod
 * created on 22.05.2016
 */
public final class Simplify {

    public static <T1 extends Number, T2 extends Number> List<Pair<T1, T2>> simplify(List<Pair<T1, T2>> points,
                        double tolerance,
                        boolean highestQuality) {

        if (points == null || points.size() <= 2) {
            return points;
        }

        double sqTolerance = tolerance * tolerance;

        if (!highestQuality) {
            points = simplifyRadialDistance(points, sqTolerance);
        }

        points = simplifyDouglasPeucker(points, sqTolerance);

        return points;
    }

    private static <T1 extends Number, T2 extends Number> List<Pair<T1, T2>> simplifyRadialDistance(List<Pair<T1, T2>> points, double sqTolerance) {
        Pair<T1, T2> point = null;
        Pair<T1, T2> prevPoint = points.get(0);

        List<Pair<T1, T2>> newPoints = new ArrayList<>();
        newPoints.add(prevPoint);

        for (int i = 1; i < points.size(); ++i) {
            point = points.get(i);

            if (getSquareDistance(point, prevPoint) > sqTolerance) {
                newPoints.add(point);
                prevPoint = point;
            }
        }

        if (prevPoint != point) {
            newPoints.add(point);
        }

        return newPoints;
    }

    private static class Range {
        private Range(int first, int last) {
            this.first = first;
            this.last = last;
        }

        int first;
        int last;
    }

    private static <T1 extends Number, T2 extends Number> List<Pair<T1, T2>> simplifyDouglasPeucker(List<Pair<T1, T2>> points, double sqTolerance) {

        BitSet bitSet = new BitSet(points.size());
        bitSet.set(0);
        bitSet.set(points.size() - 1);

        List<Range> stack = new ArrayList<>();
        stack.add(new Range(0, points.size() - 1));

        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);

            int index = -1;
            double maxSqDist = 0f;

            // find index of point with maximum square distance from first and last point
            for (int i = range.first + 1; i < range.last; ++i) {
                double sqDist = getSquareSegmentDistance(points.get(i), points.get(range.first), points.get(range.last));

                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if (maxSqDist > sqTolerance) {
                bitSet.set(index);

                stack.add(new Range(range.first, index));
                stack.add(new Range(index, range.last));
            }
        }

        List<Pair<T1, T2>> newPoints = new ArrayList<>(bitSet.cardinality());
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            newPoints.add(points.get(index));
        }

        return newPoints;
    }


    private static <T1 extends Number, T2 extends Number> double getSquareDistance(Pair<T1, T2> p1, Pair<T1, T2> p2) {

        double dx = p1.getFirst().doubleValue() - p2.getFirst().doubleValue();
        double dy = p1.getSecond().doubleValue() - p2.getSecond().doubleValue();

        return dx * dx + dy * dy;
    }

    private static <T1 extends Number, T2 extends Number> double getSquareSegmentDistance(Pair<T1, T2> p0, Pair<T1, T2> p1, Pair<T1, T2> p2) {
        double x0, y0, x1, y1, x2, y2, dx, dy, t;

        x1 = p1.getFirst().doubleValue();
        y1 = p1.getSecond().doubleValue();
        x2 = p2.getFirst().doubleValue();
        y2 = p2.getSecond().doubleValue();
        x0 = p0.getFirst().doubleValue();
        y0 = p0.getSecond().doubleValue();

        dx = x2 - x1;
        dy = y2 - y1;

        if (dx != 0.0d || dy != 0.0d) {
            t = ((x0 - x1) * dx + (y0 - y1) * dy)
                    / (dx * dx + dy * dy);

            if (t > 1.0d) {
                x1 = x2;
                y1 = y2;
            } else if (t > 0.0d) {
                x1 += dx * t;
                y1 += dy * t;
            }
        }

        dx = x0 - x1;
        dy = y0 - y1;

        return dx * dx + dy * dy;
    }
}
