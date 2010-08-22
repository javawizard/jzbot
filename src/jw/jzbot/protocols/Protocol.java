package jw.jzbot.protocols;

/**
 * A protocol that JZBot can use to communicate. Almost all of the actual functionality is
 * present on the Connection interface; this interface serves more as a factory for
 * Connection objects.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Protocol
{
    /**
     * Returns the name of this protocol.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Creates a new connection for this protocol.
     * 
     * @return
     */
    public Connection createConnection();
}
