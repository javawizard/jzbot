package jw.jzbot.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListenerManager<E>
{
    private List<E> listeners = new ArrayList<E>();
    
    private Method method;
    
    public ListenerManager(Class<E> type, String method, Class... argumentTypes)
    {
        listeners = Collections.checkedList(listeners, type);
        TODO: finish this up
    }
}
