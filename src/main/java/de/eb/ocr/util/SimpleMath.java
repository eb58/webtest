package de.eb.ocr.util;

public class SimpleMath {

    public static int sqr(int x) {
        return x * x;
    }

    public static double dsqr(double x) {
        return x * x;
    }

    public static int sqrdist(int p1[], int p2[], int mindist) {
        int res = 0;
        for (int i = 0; i < p1.length; i++) {
            final int d = (p1[i] - p2[i]);
            res += d * d;
            if (res > mindist) return res;
        }
        return res;
    }
}
