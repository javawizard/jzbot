package jw.jzbot.protocols.bzflag.t;

import java.util.Date;

import jw.jzbot.protocols.bzflag.BZFlagConnector;
import jw.jzbot.protocols.bzflag.Message;
import jw.jzbot.protocols.bzflag.ServerLink;
import jw.jzbot.protocols.bzflag.pack.MsgAccept;
import jw.jzbot.protocols.bzflag.pack.MsgEnter;
import jw.jzbot.protocols.bzflag.pack.MsgMessage;
import jw.jzbot.protocols.bzflag.pack.MsgReject;

public class BZTest01
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        final ServerLink link = new ServerLink("bzexcess.com", 5154);
        MsgEnter enter = new MsgEnter();
        enter.callsign = "marlen_jackson02";
        enter.clientVersion = "javawizard2539's JZBot, http://jzbot.googlecode.com";
        enter.email = "w00t!";
        enter.key = "";
        enter.team = 5;
        enter.type = 0;
        link.send(enter);
        new Thread()
        {
            public void run()
            {
                try
                {
                    while (true)
                    {
                        Message message = link.receive();
                        if (message instanceof MsgAccept)
                            System.out.println("Accepted.");
                        else if (message instanceof MsgReject)
                        {
                            MsgReject reject = (MsgReject) message;
                            System.out.println("Rejected: " + reject.reason + " "
                                    + reject.message);
                            link.closeIgnore();
                            System.exit(0);
                        }
                        else if (message instanceof MsgMessage)
                        {
                            MsgMessage m = (MsgMessage) message;
                            if (m.from == BZFlagConnector.MsgToServerPlayer)
                            {
                                System.out.println("Server message: " + m.message);
                            }
                            else if (m.to == link.getLocalId())
                            {
                                System.out.println("Direct message: " + m.message);
                                MsgMessage send = new MsgMessage();
                                send.to = m.from;
                                send.message = "Hi. I'm a bot that javawizard2539 is testing"
                                        + " out. Ask him if you have questions about me.";
                                link.send(send);
                            }
                            else
                            {
                                System.out.println("Broadcast message: " + m.to + " "
                                        + m.message);
                                if (m.message.equals("~ping"))
                                {
                                    MsgMessage send = new MsgMessage();
                                    send.to = m.to;
                                    send.message = "~pong";
                                    link.send(send);
                                }
                                else if (m.message.equals("~date"))
                                {
                                    MsgMessage send = new MsgMessage();
                                    send.to = m.to;
                                    send.message = "The current date/time is " + new Date()
                                            + ".";
                                    link.send(send);
                                }
                            }
                        }
                        else
                        {
                            System.out.println("Ignoring message "
                                    + message.getClass().getSimpleName());
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
