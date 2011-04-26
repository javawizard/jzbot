package jw.jzbot.crosstalk;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.ResponseException;
import jw.jzbot.configuration.Configuration;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ScopeManager;
import jw.jzbot.scope.UserMessenger;

public class Crosstalk
{
    public static final String VERSION = "1";
    
    private static final Map<String, HandlerCreator> handlerRegistry =
            new HashMap<String, HandlerCreator>();
    
    private static final Map<String, CrosstalkSession> sessions =
            new HashMap<String, CrosstalkSession>();
    
    public static void registerHandler(String name, HandlerCreator creator)
    {
        handlerRegistry.put(name, creator);
    }
    
    public static void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        if (arguments.trim().equals(""))
        {
            source.sendMessage("The __crosstalk__ command needs arguments "
                + "specified. For more help on what crosstalk is, see \"help crosstalk\".");
        }
        int originalLength = arguments.length();
        String[] tokens = arguments.split(" ", 3);
        String sessionId = tokens[0];
        String type = tokens[1];
        arguments = (tokens.length > 2 ? tokens[2] : "");
        ensureCrosstalkAllowed(sender, sessionId);
        String command = null;
        if (type.equals("c"))
        {
            tokens = arguments.split(" ", 2);
            command = tokens[0];
            arguments = (tokens.length > 1 ? tokens[1] : "");
        }
        CrosstalkSession session = getSession(sender, sessionId);
        if (session == null)
        {
            /*
             * Create a session if we're the receiver (under the assumption that this is a
             * __handshake__)
             */
        }
    }
    
    private static void ensureCrosstalkAllowed(UserMessenger sender, String session)
    {
        String[] allowed = Configuration.getText("", "crosstalkusers").split(" ");
        String senderPattern = sender.getServerName() + ":" + sender.getHostname();
        if (!StringUtils.isMemberOf(senderPattern, allowed))
            throw new ResponseException(sender.getNick()
                + ": __crosstalk__ "
                + session
                + " r __status__=error error=other message="
                + URLEncoder.encode("You're not allowed to "
                    + "crosstalk with me. See ~config global crosstalkusers."));
    }
    
    public static CrosstalkSession getSession(UserMessenger sender, String sessionId)
    {
        return sessions.get(sender.getCanonicalName() + " " + sessionId);
    }
    
    public void startCrosstalk(String server, String channel)
    {
        
    }
}
