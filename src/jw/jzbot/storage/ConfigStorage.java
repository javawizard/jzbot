package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface ConfigStorage
{
    @Property
    @ListType(ConfigVariable.class)
    public StoredList<ConfigVariable> getVariables();
    
    @Search(listProperty = "variables", searchProperty = "name")
    public ConfigVariable getVariable(String name);
    
    @Constructor
    public ConfigVariable createVariable();
}
