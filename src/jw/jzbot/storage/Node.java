package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Node
{
    @Property
    public String getName();
    
    public void setName(String name);
    
    @Property
    public String getValue();
    
    public void setValue(String value);
    
    @Property
    @ListType(Node.class)
    public StoredList<Node> getChildren();
    
    @Search(listProperty = "children", searchProperty = "name")
    public Node getChild(String name);
    
    @Constructor
    public Node createNode();
}
