package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Channel extends StorageContainer
{
    /**
     * The name of this channel. For example, this could be "#bztraining".
     * 
     * @return
     */
    @Property
    public String getName();
    
    public void setName(String name);
    
    /**
     * True if this channel is suspended. A suspended channel is one where the bot has
     * been directed to leave the channel, so it should not join the channel on startup
     * until a user issues "~join" again.
     * 
     * @return
     */
    @Property
    public boolean isSuspended();
    
    public void setSuspended(boolean suspended);
    
    /**
     * The channel's trigger. Messages starting with the channel's trigger that are sent
     * to the channel will cause the bot to execute commands.
     * 
     * @return
     */
    @Property
    public String getTrigger();
    
    public void setTrigger(String trigger);
    
    /**
     * The channel-specific factoids present at this channel.
     */
    @Property
    @ListType(Factoid.class)
    public StoredList<Factoid> getFactoids();
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = true)
    public Factoid getFactoid(String name);
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = false, anywhere = false)
    public Factoid[] searchFactoids(String search);
    
    /**
     * Returns a list of all factoids that have the specified factpack name.
     * 
     * @param factpack
     * @return
     */
    @Search(listProperty = "factoids", searchProperty = "factpack", exact = true)
    public Factoid[] getFactpackFactoids(String factpack);
    
    @Property
    @ListType(Regex.class)
    public StoredList<Regex> getRegularExpressions();
    
    @Search(listProperty = "regularExpressions", searchProperty = "expression")
    public Regex getRegex(String expression);
    
    @Property
    public ConfigStorage getConfiguration();
    
    public void setConfiguration(ConfigStorage configuration);
    
    @Constructor
    public ConfigStorage createConfiguration();

    @Property
    @ListType(StoredFunction.class)
    public StoredList<StoredFunction> getStoredFunctions();

    @Search(listProperty = "storedFunctions", searchProperty = "name", exact = true)
    public StoredFunction getStoredFunction(String name);
}
