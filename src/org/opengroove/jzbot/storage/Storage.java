package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Storage
{
    @Property
    @ListType(Channel.class)
    public StoredList<Channel> getChannels();
    
    @Search(listProperty = "channels", searchProperty = "name")
    public Channel getChannel(String name);
    
    @Property
    public Config getConfig();
    
    public void setConfig(Config config);
    
    @Constructor
    public Config createConfig();
    
    @Constructor
    public Factoid createFactoid();
    
    @Constructor
    public Operator createOperator();
    
    @Constructor
    public Channel createChannel();
    
    @Constructor
    public Regex createRegex();
    
    @Property
    @ListType(Factoid.class)
    public StoredList<Factoid> getFactoids();
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = true)
    public Factoid getFactoid(String name);
    
    @Property
    @ListType(Operator.class)
    public StoredList<Operator> getOperators();
    
    @Search(listProperty = "operators", searchProperty = "hostname")
    public Operator getOperator(String hostname);
}
