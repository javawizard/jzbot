package org.opengroove.jzbot.plugins;

/**
 * Represents an outbound message.
 * 
 * @author Alexander Boyd
 * 
 */
public class Message
{
    /**
     * True if this is to be sent as an action, false if it is to be sent as a
     * regular message. Protocols that don't support actions should emulate them
     * in some way. The easiest way to do this would be to prepend the bot's
     * username before the message, so sending "hits jcp over the head" as an
     * action might be changed to "Marlen Jackson hits jcp over the head" and
     * then sent as a regular message.
     */
    private boolean action;
    /**
     * The message to send. If {@link #action} is false, then this can contain
     * newlines. Protocols that don't support newlines should split this into
     * multiple messages. if <tt>action</tt> is true, then this will not contain
     * newlines.
     */
    private String message;
    
    public Message()
    {
        super();
    }
    
    public Message(boolean action, String message)
    {
        super();
        this.action = action;
        this.message = message;
    }
    
    public boolean isAction()
    {
        return action;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setAction(boolean action)
    {
        this.action = action;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
