package statistics;


import org.junit.Assert;

import java.util.ArrayList;

/**
 * @Author Dmitry Volovod
 * created on 16.04.2016
 */
public class StatsTest {

    @org.junit.Test
    public void testSigelTukeyTest() throws Exception {
        double[] sample1 = new double[]{1.2, 15.9, 3.4, 13.0, 6.2, 11.3, 8.1, 10.2};
        double[] sample2 = new double[]{0.8, 26.7, 24.2, 2.4, 20.1, 4.2, 13.8, 5.1, 11.4, 6.8};
//        double[] sample1 = new double[]{-1, 0, 1};
//        double[] sample2 = new double[]{-2, -3, 2, 3};
        ArrayList<Double> arrayList1 = new ArrayList<>();
        ArrayList<Double> arrayList2 = new ArrayList<>();
        for (double v : sample1) {
            arrayList1.add(v);
        }
        for (double v : sample2) {
            arrayList2.add(v);
        }
        Assert.assertEquals(true, Stats.sigelTukeyTest(arrayList1, arrayList2));
    }
}