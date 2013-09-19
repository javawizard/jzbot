package jw.jzbot.fact.output;

import jw.jzbot.fact.Sink;

/**
 * A sink that sends its output to another sink in some fashion.
 * 
 * @author Alexander Boyd
 * 
 */
public interface FilteredSink extends Sink
{
    /**
     * Gets the sink that this sink sends its output to.
     * 
     * @return
     */
    public Sink getTarget();
}
