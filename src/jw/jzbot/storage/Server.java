package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface Server extends HasFactoids
{
    /**
     * True if this server is active, false if it is not. Active servers are those that
     * should actually be connected to.
     * 
     * @return
     */
    @Property
    public boolean isActive();
    
    public void setActive(boolean active);
    
    /**
     * The list of server-specific factoids at this server.
     */
    @Property
    @ListType(Factoid.class)
    public StoredList<Factoid> getFactoids();
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = true)
    public Factoid getFactoid(String name);
    
    @Search(listProperty = "factoids", searchProperty = "name", exact = false, anywhere = false)
    public Factoid[] searchFactoids(String search);
    
    @Search(listProperty = "factoids", searchProperty = "factpack", exact = true)
    public Factoid[] getFactpackFactoids(String factpack);
    
    /**
     * The list of superops at this server.
     * 
     * @return
     */
    @Property
    @ListType(Operator.class)
    public StoredList<Operator> getOperators();
    
    @Search(listProperty = "operators", searchProperty = "hostname")
    public Operator getOperator(String hostname);
    
    /**
     * The channels present on this server.
     * 
     * @return
     */
    @Property
    @ListType(Channel.class)
    public StoredList<Channel> getChannels();
    
    /**
     * Gets a particular channel on this server by the channel's name.
     * 
     * @param name
     * @return
     */
    @Search(listProperty = "channels", searchProperty = "name")
    public Channel getChannel(String name);
    
    /**
     * The name of this server. This doesn't have to reflect at all on the server's
     * connection properties; this could, for example, be "FreenodeIRC, "freenode", etc.
     * 
     * @return
     */
    @Property
    public String getName();
    
    public void setName(String name);
    
    // SERVER CONNECTION INFO
    /**
     * The protocol to use. This is one of the keys in jw/jzbot/protocols.props (so, for
     * example, "irc" or "bzflag").
     */
    @Property
    public String getProtocol();
    
    public void setProtocol(String protocol);
    
    /**
     * The password to connect to this server with
     * 
     * @return
     */
    @Property
    public String getPassword();
    
    public void setPassword(String password);
    
    /**
     * The nickname to use at this server. This will also be used as the username.
     * 
     * @return
     */
    @Property
    public String getNick();
    
    public void setNick(String nick);
    
    /**
     * The server to connect to.
     * 
     * @return
     */
    @Property
    public String getServer();
    
    public void setServer(String server);
    
    /**
     * The port to connect to on the server.
     * 
     * @return
     */
    @Property
    public int getPort();
    
    public void setPort(int port);
    
}
