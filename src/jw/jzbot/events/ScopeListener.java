package jw.jzbot.events;

import jw.jzbot.scope.ScopeLevel;

public interface ScopeListener
{
    public void notify(ScopeLevel level, String scope, boolean initial);
}
