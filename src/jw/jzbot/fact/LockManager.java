package jw.jzbot.fact;

import java.util.HashMap;

/**
 * A class that manages named locks for the Fact system. At some point, this will be made
 * into an instance class instead of a class with static methods.
 * 
 * @author Alexander Boyd
 * 
 */
public class LockManager
{
    private static HashMap<String, Lock> lockMap = new HashMap<String, Lock>();
    
    public static Lock obtain(String name)
    {
        synchronized (lockMap)
        {
            Lock lock = lockMap.get(name);
            if (lock == null)
            {
                lock = new Lock(name);
                lockMap.put(name, lock);
            }
            lock.references.incrementAndGet();
            return lock;
        }
    }
    
    public static void release(String name, Lock lock)
    {
        synchronized (lockMap)
        {
            lock.references.decrementAndGet();
            if (lock.references.get() <= 0)
            {
                lockMap.remove(name);
            }
        }
    }
}
