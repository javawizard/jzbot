package jw.jzbot.fact;

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
     * Resolves all of the children of this sequence, concatenates their output together,
     * and returns it.
     */
    @Override
    public void execute(Sink sink, FactContext context)
    {
        /*
         * Due to the fact that the parser wraps a lot of single entities with a sequence,
         * this makes everything run faster if we check for sequences that have only one
         * element in them.
         */
        for (FactEntity entity : list)
        {
            entity.resolve(sink, context);
        }
    }
    
    @Override
    public void explain(Sink sink, int indentation, int increment)
    {
        explain(sink, indentation, increment, true);
    }
    
    public void explain(Sink sink, int indentation, int increment, boolean prefix)
    {
        if (prefix)
            sink.add(spaces(indentation));
        sink.add("sequence:\n");
        for (FactEntity entity : list)
        {
            entity.explain(sink, indentation + increment, increment);
        }
    }
    
    public void remove(int index)
    {
        list.remove(index);
    }
}
