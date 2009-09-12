package org.opengroove.jzbot.fact;

import java.util.ArrayList;

public class Sequence
{
    private ArrayList<FactEntity> list = new ArrayList<FactEntity>();
    
    public int length()
    {
        return list.size();
    }
    
    public FactEntity get(int index)
    {
        return list.get(index);
    }
}
