/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fd;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author barcick
 */
public final class FixMethods {

    private FixMethods() {
    }

    //Ivan's idt with my stub
    public static ArrayList<Boolean> calculate(ArrayList<Double> array, int th, int originInterval) {
        ArrayList<Boolean> result = new ArrayList<>(Collections.nCopies(array.size(), Boolean.FALSE));
        //:TODO stub
//        for (int i = 0; i < array.size(); i++) {
//            result.add(Boolean.FALSE);
//        }
        int start = 1;
        int interval = originInterval;
        while (start + interval < array.size()) {
            if (dispersion(array, start, interval) <= th) {
                while (dispersion(array, start, interval) <= th) {
                    interval++;
                    if (start + interval >= array.size()) {
                        break;
                    }
                }
                interval--;
                for (int i = start; i < start + interval; i++) {
                    if (start + interval >= array.size()) {
                        break;
                    }
                    result.set(i, Boolean.TRUE);
                    System.out.print(i + "  ");//индекс точки с фиксацией
                    System.out.println(array.get(i));//значение точки с фиксацией
                }
                start += interval;
                interval = originInterval;
            } else {
                start++;
            }
        }
        return result;
    }

    //my IDT
    public static ArrayList<Boolean> idt(ArrayList<Double> array, int th, int interval) {
        ArrayList<Boolean> result = new ArrayList<>(Collections.nCopies(array.size(), Boolean.FALSE));
        int wt = interval;
        int start = 0;
        while (start + wt <= array.size()) {
            if (dispersion(array, start, wt) <= th) {
                while (dispersion(array, start, wt) <= th && (start + wt) < array.size()) {
                    wt++;
                }
                wt--;
                for (int i = start; i < start + wt; i++) {
//                    System.out.print(i + "  ");
//                    System.out.println("true");
                    result.set(i, Boolean.TRUE);
                }
                start += wt;
                wt = interval;
            } else {
                for (int i = start; i < start + wt; i++) {
//                    System.out.print(i + "  ");
//                    System.out.println("false");
                    result.set(i, Boolean.FALSE);
                }
                start = start + wt;
                wt = interval;

            }
        }
        System.out.println(result);
        return result;
    }

    public static void idt2(ArrayList<ArrayList<Double>> arrayList, int th, int interval) {
        int wt = interval;
        int start = 0;
        for (ArrayList<Double> array : arrayList) {

            while (start + wt <= array.size()) {
                if (dispersion(array, start, wt) <= th) {
                    while (dispersion(array, start, wt) <= th && (start + wt) < array.size()) {
                        wt++;
                    }
                    wt--;
                    for (int i = start; i < start + wt; i++) {
                        System.out.print(i + "  ");
                        System.out.print(array.get(i));
                        System.out.println("true");
//                        mfile.write(i + ";" + array.get(i) + ";" + "true" + System.lineSeparator());
//                        mfile.write(Counterr.count[i] + ";" + y[i] + ";" + "true" + System.lineSeparator());
                    }
                    start += wt;
                    wt = interval;
                } else {
                    for (int i = start; i < start + wt; i++) {
                        System.out.print(i + "  ");
                        System.out.println(array.get(i));
                        System.out.println("false");
//                        mfile.write(i + ";" + array.get(i) + ";" + "false" + System.lineSeparator());
                    }
                    start = start + wt;
                    wt = interval;

                }
            }
        }

    }

    private static double dispersion(ArrayList<Double> array, int start, int n) {
        double ysr = 0, res = 0;
        for (int i = start; i < start + n; i++) {
            ysr += array.get(i);
        }
        ysr = ysr / n;
        for (int i = start; i < start + n; i++) {
            res += Math.pow((array.get(i) - ysr), 2);
        }
        res = res / n;
        return res;
    }
}