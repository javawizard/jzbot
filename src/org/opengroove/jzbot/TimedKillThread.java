package org.opengroove.jzbot;

public class TimedKillThread extends Thread
{
    public volatile boolean active = true;
    private Thread target;
    
    public static final int MAX_FACT_RUN_TIME = 1000 * 30;
    
    public volatile int maxRunTime = MAX_FACT_RUN_TIME;
    
    public TimedKillThread(Thread target)
    {
        this.target = target;
    }
    
    @SuppressWarnings("deprecation")
    public void run()
    {
        try
        {
            Thread.sleep(maxRunTime);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (active)
            target.stop(new FactTimeExceededError(
                    "This factoid or command took too long to run. Factoids and "
                            + "commands can only run for "
                            + (MAX_FACT_RUN_TIME / 1000) + " seconds."));
    }
}
