/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eb.ocr.util;

/**
 *
 * @author de1usrs0
 */
public final class StopWatch {

    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;

    public StopWatch() {
        start();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
        running = false;
    }

    public long getElapsedTimeInMilliSecs() {
        return running ? System.currentTimeMillis() - startTime : stopTime - startTime;
    }

    public long getElapsedTimeInSecs() {
        return getElapsedTimeInMilliSecs() / 1000;
    }
}
