package org.opengroove.jzbot.fact;

import java.util.ArrayList;

public class Sequence extends FactEntity
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
    
    public void add(FactEntity entity)
    {
        list.add(entity);
    }
    
    /**
     * Resolves all of the children of this sequence, concatenates their output
     * together, and returns it.
     */
    @Override
    public String resolve(FactContext context)
    {
        /*
         * Due to the fact that the parser wraps a lot of single entities with a
         * sequence, this makes everything run faster if we check for sequences
         * that have only one element in them.
         */
        if (list.size() == 1)
            return list.get(0).resolve(context);
        StringBuffer buffer = new StringBuffer();
        for (FactEntity entity : list)
        {
            buffer.append(entity.resolve(context));
        }
        return buffer.toString();
    }
}
