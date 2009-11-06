package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface MapContainer
{
    @Property
    @ListType(MapEntry.class)
    public StoredList<MapEntry> getEntries();
    
    @Search(listProperty = "entries", searchProperty = "key")
    public MapEntry getEntry(String key);
    
    @Constructor
    public MapEntry createEntry();
}
