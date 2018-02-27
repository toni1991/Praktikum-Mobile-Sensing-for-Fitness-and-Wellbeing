package de.uni_augsburg.mobilesensingforfitnessandwellbeing.util;

import java.util.LinkedList;

/**
 * Created by lukas on 27.02.18.
 */

public class DSPUitility {

    public static double[] getHammingWindowFactors(int windowSize) {
        // precompute factors for given window, avoid re-calculating for several instances
        double[] factors;

        factors = new double[windowSize];
        int sizeMinusOne = windowSize - 1;
        for (int i = 0; i < windowSize; i++) {
            factors[i] = 0.54d - (0.46d * Math.cos((Math.PI * 2 * i) / sizeMinusOne));
        }
        return factors;
    }

    public static double calculateShortTermEnergy(LinkedList<Float> signal, int start, int windowlength )
    {
        if (signal.size() - start - windowlength < 0)
            throw new IndexOutOfBoundsException("start and window length are outside of the signal");

        double factors[] = getHammingWindowFactors(windowlength);
        double totalEnergy = 0;

        double total = 0;
        int count = 0;
        for (int i = start; i < start+windowlength; i++)
        {
            total += signal.get(i);
            count++;
        }

        double average = total / count;

        for (int i = start; i < start+windowlength; i++)
        {
            totalEnergy += factors[i-start] * (Math.pow(signal.get(i)-average,2));
        }
        return totalEnergy;
    }

}
