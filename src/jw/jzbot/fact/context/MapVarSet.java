package jw.jzbot.fact.context;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An implementation of {@link VarSet} that provides access as a VarSet to a
 * Map<String,String>.
 * 
 * @author Alexander Boyd
 * 
 */
public class MapVarSet implements VarSet
{
    private Map<String, String> map;
    
    public MapVarSet(Map<String, String> map)
    {
        this.map = map;
    }
    
    @Override
    public String get(String name)
    {
        return map.get(name);
    }
    
    @Override
    public String[] list(String regex)
    {
        if (regex == null)
            return map.keySet().toArray(new String[0]);
        ArrayList<String> results = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        for (String key : map.keySet())
        {
            if (pattern.matcher(key).matches())
                results.add(key);
        }
        return results.toArray(new String[0]);
    }
    
    @Override
    public void set(String name, String value)
    {
        if (value == null)
            map.remove(name);
        else
            map.put(name, value);
    }
    
}
