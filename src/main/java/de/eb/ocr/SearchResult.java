package de.eb.ocr;

public class SearchResult {

    public static final int DIFFMAXVAL = Integer.MAX_VALUE / 1024;

    class Result {

        public char c = '~';           // Klassifiziert als dieses Zeichen
        public int dist = DIFFMAXVAL;  // min Abstand zu allen Zeichen in DB
    }
    Result best = new Result();
    Result secbest = new Result();
    double conf = 0;

    public void update(final char c, final int dist) {
        if (dist >= secbest.dist) return;
        if (dist < best.dist)
            if (c == best.c)
                best.dist = dist;
            else {
                secbest.c = best.c;
                secbest.dist = best.dist;
                best.c = c;
                best.dist = dist;
            }
        else if (c != best.c) {
            secbest.dist = dist;
            secbest.c = c;
        }
    }

    public int getMinDist() {
        return best.dist;
    }

    public char getBestChar() {
        return best.c;
    }

    public char getSecondBestChar() {
        return secbest.c;
    }

    double getMQ() {
        return (double) secbest.dist / best.dist;
    }

    void setConf(double d) {
        conf = d;
    }

    double getConf() {
        return conf;
    }
}
