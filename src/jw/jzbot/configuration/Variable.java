package jw.jzbot.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jw.jzbot.configuration.Configuration.VarType;

public class Variable extends Setting
{
    public String defaultValue;
    public volatile String value;
    public List<VarListener> listeners = new ArrayList<VarListener>();
    public List<VarFilter> filters = new ArrayList<VarFilter>();
    
    public Variable(String name, String description, VarType type, String defaultValue,
            String value)
    {
        super(name, description, type);
        this.defaultValue = defaultValue;
        this.value = value;
    }
    
    public void fireListeners(String scope, String name)
    {
        ArrayList<VarListener> newList = new ArrayList<VarListener>();
        synchronized (listeners)
        {
            newList.addAll(listeners);
        }
        for (VarListener listener : newList)
        {
            try
            {
                listener.changed(scope, name);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    public void fireFilters(String scope, String name, String value)
    {
        ArrayList<VarFilter> newList = new ArrayList<VarFilter>();
        synchronized (filters)
        {
            newList.addAll(filters);
        }
        for (VarFilter filter : newList)
        {
            /*
             * We specifically do not want to catch exceptions here. The idea of a filter
             * is that if the value isn't allowed it will throw an exception that will
             * propagate back up and prevent it from being set.
             */
            filter.filter(scope, name, value);
        }
    }
    
    /**
     * Validates that the specified text is permissible as a value for this variable given
     * this variable's type. An exception will be thrown if this value does not work.
     * 
     * @param text
     */
    public void validateConformantValue(String text)
    {
        boolean works = false;
        try
        {
            if (type == VarType.bool)
            {
                Integer.parseInt(text);
                works = true;
            }
            else if (type == VarType.decimal)
            {
                new BigDecimal(text);
                works = true;
            }
            else if (type == VarType.folder)
            {
                throw new IllegalArgumentException("You can't set a value for a folder.");
            }
            else if (type == VarType.integer)
            {
                new BigInteger(text);
                works = true;
            }
            else if (type == VarType.text)
            {
                works = true;
            }
            else if (type == null)
            {
                throw new IllegalArgumentException(
                        "You can't set the parent configuration folder's value.");
            }
        }
        catch (Exception e)
        {
            if (e.getClass() == IllegalArgumentException.class)
                throw (IllegalArgumentException) e;
            e.printStackTrace();
        }
        if (!works)
            throw new IllegalArgumentException(
                    "That's not a valid value for a configuration variable of type "
                        + type.name());
    }
}
