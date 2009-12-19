package jw.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Config
{
    // None of these properties are needed anymore. Once I'm certain everything with the
    // new bot is up and working, I'll get rid of the Config class altogether.
    
    @Property
    @Default(stringValue = "jw.jzbot.protocols.IrcProtocol")
    public String getProtocol();
    
    public void setProtocol(String protocol);
    
    @Property
    public String getPassword();
    
    public void setPassword(String password);
    
    @Property
    public String getNick();
    
    public void setNick(String nick);
    
    @Property
    public String getServer();
    
    public void setServer(String server);
    
    @Property
    public int getPort();
    
    public void setPort(int port);
    
    // @Property
    // @Default(stringValue = "jeval")
    // public String getEvalEngine();
    //    
    // public void setEvalEngine(String evalEngine);
    //    
    // @Property
    // public String getCharset();
    //    
    // public void setCharset(String charset);
}
