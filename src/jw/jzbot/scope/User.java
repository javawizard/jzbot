package jw.jzbot.scope;

public interface User extends Messenger
{
    /**
     * Returns true if this user is a superop or false if they are not. If this user
     * object doesn't possess enough information to know whether or not the user is a
     * superop, this method should throw an exception.
     * 
     * @return
     */
    public boolean isSuperop();
    
    /**
     * Throws an exception if {@link #isSuperop()} returns false. If this user object
     * doesn't possess enough information to know whether or not the user is a superop,
     * this method should throw an exception.
     */
    public void verifySuperop();
    
    /**
     * Returns this user's nickname.
     * 
     * @return
     */
    public String getNick();
    
    /**
     * Returns this user's hostname, or throws an exception if the user's hostname is not
     * currently known.
     * 
     * @return
     */
    public String getHostname();
    
    /**
     * Returns this user's username, or throws an exception if the user's username is not
     * currently known.
     * 
     * @return
     */
    public String getUsername();
}
