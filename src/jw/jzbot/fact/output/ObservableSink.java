package jw.jzbot.fact.output;

import jw.jzbot.fact.Sink;

/**
 * A sink that internally buffers data written to it and makes it available as a String.
 * Sinks generally should not extend this interface unless they already buffer data
 * written to them. In other words, if a sink does not already buffer data, it should not
 * add buffering support just to be able to implement this interface.
 * 
 * @author Alexander Boyd
 * 
 */
public interface ObservableSink extends Sink
{
    public String getCurrentValue();
}
