package jw.jzbot.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListenerManager<E>
{
    private List<E> listeners = new ArrayList<E>();
    
    private Method method;
    private String methodName;
    private Class[] argumentTypes;
    private Class type;
    
    public ListenerManager(Class<E> type, String method, Class... argumentTypes)
    {
        this.type = type;
        listeners = Collections.checkedList(listeners, type);
        this.methodName = method;
        this.argumentTypes = argumentTypes;
        try
        {
            this.method = type.getMethod(method, argumentTypes);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("No such method " + method, e);
        }
    }
    
    public ListenerManager copy()
    {
        return new ListenerManager(type, methodName, argumentTypes);
    }
    
    public void addListener(E listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }
    
    public void removeListener(E listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }
    
    public void fireListeners(Object... arguments)
    {
        ArrayList<E> newList = new ArrayList<E>();
        synchronized (listeners)
        {
            newList.addAll(listeners);
        }
        for (E listener : newList)
        {
            try
            {
                method.invoke(listener, arguments);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
