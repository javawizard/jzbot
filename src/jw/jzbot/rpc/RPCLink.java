package jw.jzbot.rpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Represents a bi-directional RPC link.
 * 
 * @author Alexander Boyd
 * 
 */
public class RPCLink<E>
{
    private E clientInstance;
    private Class<E> clientInterface;
    private Object serverInterface;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    public RPCLink(Socket socket, Class<E> clientInterface, Object serverInterface)
            throws IOException
    {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.clientInterface = clientInterface;
        this.serverInterface = serverInterface;
        constructClientProxy();
    }
    
    private void constructClientProxy()
    {
        
    }
}
