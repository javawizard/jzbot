package org.jibble.pircbot;

public interface User
{
    public boolean isOp();
    
    public boolean isHalfop();
    
    public boolean isAdmin();
    
    public boolean isFounder();
    
    public boolean hasVoice();
    
    public String getNick();
}
