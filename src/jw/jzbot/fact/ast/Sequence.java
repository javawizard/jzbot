package jw.jzbot.fact.ast;

import java.util.ArrayList;

import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

public class Sequence extends FactEntity
{
    /**
     * If this sequence is the list of arguments to a function, then this is the function,
     * and the sequence will not have a parent. Otherwise, this is null and the sequence
     * will have a parent (unless it's the top-level debug point).
     */
    private FunctionReference function;
    
    public FunctionReference getFunction()
    {
        return function;
    }
    
    /**
     * Sets the function reference for this sequence, and changes all children of this
     * sequence to have the specified function reference as their parent.
     * 
     * @param function
     */
    public void setFunction(FunctionReference function)
    {
        this.function = function;
        for (FactEntity child : list)
        {
            child.setParent(function);
        }
    }
    
    private ArrayList<FactEntity> list = new ArrayList<FactEntity>();
    
    public int length()
    {
        return list.size();
    }
    
    public FactEntity get(int index)
    {
        return list.get(index);
    }
    
    /**
     * Adds the specified item to this sequence and sets the parent of the specified item
     * to this sequence.
     * 
     * @param entity
     */
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
            sink.write(spaces(indentation));
        sink.write("sequence:\n");
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
