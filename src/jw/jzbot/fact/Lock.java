package jw.jzbot.fact;

import java.util.concurrent.atomic.AtomicLong;

public class Lock
{
    private String name;
    public final AtomicLong references = new AtomicLong(0);
    
    Lock(String name)
    {
        this.name = name;
    }
    
    public void release()
    {
        LockManager.release(name, this);
    }
}
