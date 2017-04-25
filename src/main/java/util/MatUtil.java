package util;

import io.hummer.util.math.MathUtil;

import java.util.List;

import static java.util.Comparator.naturalOrder;

/**
 * Created by mac on 24/04/17.
 */
public class MatUtil {


    public static final double SIGNIFICANCE_LEVEL = 0.9;

    public static int ortalamaHesapla(List<Integer> sayilar) {

        sayilar.sort(naturalOrder());

        MathUtil mathUtil = new MathUtil();

        while (mathUtil.getOutlier(sayilar, SIGNIFICANCE_LEVEL) != null) {
            double average = mathUtil.average(sayilar);
            Integer outlier = mathUtil.getOutlier(sayilar, SIGNIFICANCE_LEVEL);
            if (outlier > average) {
                sayilar = sayilar.subList(0, sayilar.size() - 1);
            } else {
                sayilar = sayilar.subList(1, sayilar.size());
            }
        }

        int average = (int) mathUtil.average(sayilar);
        if (average == 0)
            average = 1;
        return average;
    }
}
