package jw.jzbot.rpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Represents a bi-directional RPC link.
 * 
 * @author Alexander Boyd
 * 
 */
public class RPCLink<E>
{
    private class ProxyHandler implements InvocationHandler
    {
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            return null;
        }
        
    }
    
    private class InputThread extends Thread
    {
        
    }
    
    private class OutputThread extends Thread
    {
        
    }
    
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
