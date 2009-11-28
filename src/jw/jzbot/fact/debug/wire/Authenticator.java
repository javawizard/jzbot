package jw.jzbot.fact.debug.wire;

public interface Authenticator
{
    /**
     * Asks this authenticator to create an authentication request.
     * 
     * @return the request
     */
    public AuthRequest getRequest();
    
    /**
     * Asks this authenticator to validate the responses to a request. The authenticator
     * should return a name for the connection if the values are correct. If the values
     * are not correct (for example, the user sent an incorrect password), then an
     * AccessDeniedException should be thrown.<br/><br/>
     * 
     * This method should never return the same name twice. For example, if this
     * authenticator authenticates off of usernames or some such construct, it could
     * append a number to the end of the name representing how many times the user has
     * authenticated since the authenticator was created.
     * 
     * @param values
     * @return A name for this connection if the values are correct
     * @throws AccessDeniedException
     *             if the values provided are not correct
     */
    public String authenticate(String[] values) throws AccessDeniedException;
}
