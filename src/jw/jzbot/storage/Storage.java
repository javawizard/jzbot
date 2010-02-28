package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Storage extends HasFactoids
{
    @Property
    @ListType(Server.class)
    public StoredList<Server> getServers();
    
    @Search(listProperty = "servers", searchProperty = "name")
    public Server getServer(String name);
    
    @Property
    public Config getConfig();
    
    public void setConfig(Config config);
    
    @Property
    @ListType(MapEntry.class)
    public StoredList<MapEntry> getPersistentVariables();
    
    @Search(listProperty = "persistentVariables", searchProperty = "key")
    public MapEntry getPersistentVariable(String key);
    
    @Property
    @ListType(MapEntry.class)
    public StoredList<MapEntry> getConfigVars();
    
    @Search(listProperty = "configVars", searchProperty = "key")
    public MapEntry getConfigVar(String key);
    
    @Property
    @ListType(MapEntry.class)
    public StoredList<MapEntry> getRedefinitions();
    
    /**
     * Returns the redefinition that has the specified key, or new command name.
     * 
     * @param key
     * @return
     */
    @Search(listProperty = "redefinitions", searchProperty = "key")
    public MapEntry getRedefinitionByKey(String key);
    
    /**
     * Returns the redefinition that has the specified value, or target command.
     * 
     * @param value
     * @return
     */
    @Search(listProperty = "redefinitions", searchProperty = "value")
    public MapEntry getRedefinitionByValue(String value);
    
    @Constructor
    public MapEntry createMapEntry();
    
    @Constructor
    public Config createConfig();
    
    @Constructor
    public Factoid createFactoid();
    
    @Constructor
    public Operator createOperator();
    
    @Constructor
    public Server createServer();
    
    @Constructor
    public Channel createChannel();
    
    @Constructor
    public Regex createRegex();
    
    @Constructor
    public PersistentKey createPersistentKey();
    
    @Property
    @ListType(Factoid.class)
    public StoredList<Factoid> getFactoids();
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = true)
    public Factoid getFactoid(String name);
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = false, anywhere = false)
    public Factoid[] searchFactoids(String search);
    
    @Search(listProperty = "factoids", searchProperty = "factpack", exact = true)
    public Factoid[] getFactpackFactoids(String factpack);
    
    @Property
    @ListType(PersistentKey.class)
    public StoredList<PersistentKey> getPluginKeys();
    
    @Search(listProperty = "pluginKeys", searchProperty = "key")
    public PersistentKey getPluginKey(String key);
    
    @Search(listProperty = "pluginKeys", searchProperty = "name")
    public PersistentKey getPluginKeyByName(String string);
}
