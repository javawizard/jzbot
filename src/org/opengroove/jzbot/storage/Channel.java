package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Channel
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public boolean isSuspended();
    
    public void setSuspended(boolean suspended);
    
    @Property
    public String getJoinFactoid();
    
    public void setJoinFactoid(String name);
    
    @Property
    public String getTrigger();
    
    public void setTrigger(String trigger);
    
    @Property
    @ListType(Factoid.class)
    public StoredList<Factoid> getFactoids();
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = true)
    public Factoid getFactoid(String name);
    
    @Property
    @ListType(Operator.class)
    public StoredList<Operator> getOperators();
    
    @Property
    @ListType(Regex.class)
    public StoredList<Regex> getRegularExpressions();
    
    @Search(listProperty = "regularExpressions", searchProperty = "expression")
    public Regex getRegex(String expression);
    
    @Search(listProperty = "operators", searchProperty = "hostname")
    public Operator getOperator(String hostname);
    
}
