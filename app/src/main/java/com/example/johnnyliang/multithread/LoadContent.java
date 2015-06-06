package com.example.johnnyliang.multithread;

/**
 * Created by JohnnyLiang on 6/2/15.
 */
public class LoadContent implements Runnable {

    MyActivity activity;
    boolean stop = false;

    public void setActivity(MyActivity myActivity) {
        activity = myActivity;
    }

    public void setStop(boolean stopped) {
        stop = stopped;
    }

    @Override
    public void run() {
        while (!activity.getStop()) {
            activity.loadAndParse();
        }

        System.out.println("Closing up shop...");
    }
}
