package jw.jzbot.scope;

/**
 * An interface representing an object that can send or receive messages. Messengers
 * typically represent individual users or channels.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Messenger
{
    /**
     * Sends a message to this messenger.
     * 
     * @param message
     */
    public void sendMessage(String message);
    
    /**
     * Sends an action to this messenger.
     * 
     * @param action
     */
    public void sendAction(String action);
    
    /**
     * Gets the maximum size of a message that can be reliably sent to this messenger.
     * Longer messages may (but are not guaranteed to) be truncated.
     * 
     * @return
     */
    public int getProtocolDelimitedLength();
    
    /**
     * Sends a message as several chunks, each no longer than the protocol delimited
     * length. Messages will be split into chunks around space characters.
     * 
     * @param message
     */
    public void sendSpaced(String message);
    
    /**
     * Returns true if this protocol prefers long pieces of text to be pastebinned, false
     * if it prefers them to be sent as several consecutive messages. For example, IRC
     * prefers pastebin posts because it's less spammy and pastebin URLs can be easily
     * opened from most IRC clients, whereas BZFlag does not prefer pastebin because it's
     * quite difficult to open a URL sent in a BZFlag message in a browser and BZFlag
     * doesn't lag or throttle messages so sending a whole bunch of consecutive messages
     * usually isn't a problem.
     * 
     * @return
     */
    public boolean likesPastebin();
    
    /**
     * Returns the scope name for this messenger. This should be the smallest valid scope
     * that the messenger is contained in or represents. If this is a channel, this will
     * be the channel's scope. If this is a user, this will be the scope of the server
     * that the user resides on.
     * 
     * @return
     */
    public String getScopeName();
}
