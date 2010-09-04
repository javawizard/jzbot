package jw.jzbot.scope;

import jw.jzbot.utils.Utils;

/**
 * An interface representing an object that can send or receive messages. Messengers
 * typically represent individual users or channels.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Messenger extends Agent
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
     * length. Messages will be split into chunks around space characters. Implementations
     * of this interface can simply call {@link Utils#sendSpaced(Messenger, String)},
     * passing in this messenger object and the specified string, and it will take care of
     * splitting the string up and invoking {@link #sendMessage(String)} on each
     * component.
     * 
     * @param message
     */
    public void sendSpaced(String message);
    
    /**
     * Returns true if this messenger prefers long pieces of text to be pastebinned, false
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
    
    // FIXME: add getConnection() and getProtocol() if there's any feasible use for them
    
}
