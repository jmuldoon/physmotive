package edu.usf.cse.physmotive.logic;

import android.os.Handler;
import edu.usf.cse.physmotive.ActiveActivity;

public class StopWatch {    
    private static long startTimePoint, tTime = 0;
    private static long DELAY = 100;
    private static String applicationState;
    private static Handler tasksHandler = new Handler();

	
    public static void startCounting()
    {
        applicationState = StopWatchStates.IN_COUNTING;

        tasksHandler.removeCallbacks(timeTickRunnable);
        tasksHandler.postDelayed(timeTickRunnable, DELAY);

        startTimePoint = System.nanoTime();
    }

    public static void stopCounting()
    {
        applicationState = StopWatchStates.IN_WAITING;
    }
    
    public static void setStartTimePoint(Long num){
    	startTimePoint = num;
    }

    public static String currentTimeString()
    {
        long interval = System.nanoTime() - startTimePoint;
        tTime = (int) (interval / 1000000000);
        int minutes = ((int) tTime) / 60;
        int hours = minutes / 60;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes % 60) + ":"
                + String.format("%02d", tTime % 60);
    }

    public static double currentTime()
    {
        double interval = System.nanoTime() - startTimePoint;
        return (interval/1000000000);
    }
    
    private static Runnable timeTickRunnable = new Runnable() {
        public void run()
        {
            if (applicationState == StopWatchStates.IN_COUNTING)
            {
                setLabelText(currentTimeString());
                tasksHandler.postDelayed(timeTickRunnable, DELAY);
            }
        }
    };
    
    public static void setLabelText(String string){
    	ActiveActivity.getTimeLabel().setText(string);
    }

    public class StopWatchStates
    {
        public static final String IN_COUNTING = "StopWatchStates.IN_COUNTING";
        public static final String IN_WAITING = "StopWatchStates.IN_WAITING";
    }
}
