package org.opengroove.jzbot;

public class TimedKillThread extends Thread
{
    public volatile boolean active = true;
    private Thread target;
    
    public static final int MAX_FACT_RUN_TIME = 1000 * 20;
    
    public TimedKillThread(Thread target)
    {
        this.target = target;
    }
    
    public void run()
    {
        try
        {
            Thread.sleep(MAX_FACT_RUN_TIME);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (active)
            target.stop(new FactTimeExceededError());
    }
}
