package jw.jzbot.fact.debug;

import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.context.VarSet;
import jw.jzbot.fact.context.VarSet.Type;

public interface DebugInstance
{
    /**
     * Indicates to this debug instance that the interpreter is about to execute the
     * specified entity. Debuggers should generally ignore this call if the entity's
     * {@link FactEntity#isOmitFromStack() isOmitFromStack()} method returns true.
     * 
     * @param entity
     *            The entity that the interpreter is about to execute
     */
    public void entering(FactEntity entity);
    
    /**
     * Indicates to this debug instance that the interpreter has just finished executing
     * the specified entity. Debuggers should generally ignore this call if the entity's
     * {@link FactEntity#isOmitFromStack() isOmitFromStack()} method returns true.
     * 
     * @param entity
     *            The entity that the interpreter has just finished executing
     */
    public void exiting(FactEntity entity);
    
    /**
     * Indicates to this debug instance that an exception was thrown while executing the
     * specified entity. This is called in lieu of {@link #exiting(FactEntity)}, and at
     * the point at which <tt>exiting</tt> would normally have been called had the error
     * not occurred. Debuggers should generally ignore this call if the entity's
     * {@link FactEntity#isOmitFromStack() isOmitFromStack()} method returns true.
     * 
     * @param entity
     *            The entity that threw the exception
     * @param error
     *            The exception that was thrown while executing the entity
     */
    public void error(FactEntity entity, Throwable error);
    
    public VarSet wrap(VarSet set, Type type);
}
