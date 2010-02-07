package jw.jzbot.protocols.xmpp.t;

import org.jivesoftware.smack.XMPPConnection;

public class Test01
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        XMPPConnection con = new XMPPConnection("opengroove.org");
        con.connect();
        con.login(UserInfo.username, UserInfo.password);
        
    }
    
}
