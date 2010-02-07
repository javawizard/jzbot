package jw.jzbot.protocols.xmpp.t;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

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
        Roster roster = con.getRoster();
        for (RosterEntry e : roster.getEntries())
        {
            System.out.println("Name: " + e.getName() + ", User: " + e.getUser()
                + ", Type: " + e.getType().name());
        }
        System.out.println("------------------------");
        ChatManager cm = con.getChatManager();
        Chat chat = cm.createChat("mrdudle@dudle.us", new MessageListener()
        {
            
            @Override
            public void processMessage(Chat chat, Message message)
            {
                System.out.println("Message: " + message.getError());
            }
        });
        chat
                .sendMessage("Hi from Alex. Note that you distinctly did not see a \"anotheruser wishes to chat with you. OK?\" box.");
        Thread.sleep(5000);
        con.disconnect();
    }
    
}
