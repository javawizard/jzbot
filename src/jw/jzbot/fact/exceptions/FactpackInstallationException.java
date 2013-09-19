package jw.jzbot.fact.exceptions;

/**
 * A factoid exception that is treated normally unless it occurs while running a
 * preinstall or preuninstall script for a factpack, in which case it causes the
 * installation or uninstallation of the factpack to be aborted and the exception's
 * message to be sent to the user. Inserting newlines into the message causes multiple IRC
 * messages to be sent by the bot in regard to the error. This is almost exclusively used
 * by dependency verification functions that can be run as part of the preinstall script.
 * It can be manually thrown from within a factpid by use of the {fpabort} function.
 * 
 * @author Alexander Boyd
 * 
 */
public class FactpackInstallationException extends FactoidException
{
    
    public FactpackInstallationException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public FactpackInstallationException(String message)
    {
        super(message);
    }
    
    public FactpackInstallationException(Throwable cause)
    {
        super(cause);
    }
    
}
