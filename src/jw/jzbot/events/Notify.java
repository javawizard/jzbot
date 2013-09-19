package jw.jzbot.events;

import java.util.ArrayList;

import jw.jzbot.scope.ScopeLevel;

/**
 * This class can be used to listen for some common events occurring through JZBot. Right
 * now the events are hard-coded, but I might make it possible to dynamically extend the
 * set of events available at some later date.<br/><br/>
 * 
 * Listeners are added and removed from the listener managers present on this class. The
 * portions of JZBot responsible for dealing with triggers of the specified events will
 * notify the listener managers when they should call their corresponding events.
 * 
 * @author Alexander Boyd
 * 
 */
public class Notify
{
    public static final ListenerManager<ScopeListener> channelAdded =
            new ListenerManager<ScopeListener>(ScopeListener.class, "notify",
                    ScopeLevel.class, String.class, Boolean.TYPE);
    public static final ListenerManager<ScopeListener> channelRemoved = channelAdded.copy();
    public static final ListenerManager<ScopeListener> serverAdded = channelAdded.copy();
    public static final ListenerManager<ScopeListener> serverRemoved = channelAdded.copy();
}
