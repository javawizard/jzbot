package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Regex
{
    @Property
    public String getExpression();
    
    public void setExpression(String expression);
    
    @Property
    public String getFactoid();
    
    public void setFactoid(String factoid);
}
