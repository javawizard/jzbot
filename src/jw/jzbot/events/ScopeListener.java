package jw.jzbot.events;

import jw.jzbot.ScopeLevel;

public interface ScopeListener
{
    public void notify(ScopeLevel level, String scope, boolean initial);
}
