package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

public interface StorageContainer
{
    public StoredList<Factoid> getFactoids();
    
    public Factoid getFactoid(String name);
    
    public Factoid[] getFactpackFactoids(String factpack);
    
    public Factoid[] searchFactoids(String search);
    
    public ConfigStorage getConfiguration();
    
    public ConfigStorage createConfiguration();
    
    public void setConfiguration(ConfigStorage storage);
}
