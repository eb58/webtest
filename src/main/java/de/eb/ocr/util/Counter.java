package de.eb.ocr.util;

public class Counter {

    private int cnt = 0;

    public Counter() {
        cnt = 0;
    }

    public Counter(int n) {
        cnt = n;
    }

    public void inc() {
        cnt++;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int n) {
        cnt = n;
    }
}
