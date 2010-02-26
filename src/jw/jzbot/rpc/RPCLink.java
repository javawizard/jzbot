package jw.jzbot.rpc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a bi-directional RPC link.
 * 
 * @author Alexander Boyd
 * 
 */
public class RPCLink<E>
{
    private static final Object nullObject = new Object();
    
    public static final AtomicLong idSequence = new AtomicLong();
    
    private class ProxyHandler implements InvocationHandler
    {
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            if (socket.isClosed() || !socket.isConnected())
                throw new IllegalStateException("The connection to the RPC "
                    + "server has either not yet been opened or has been "
                    + "closed, so you can't call any RPC methods.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            DataOutputStream dos = new DataOutputStream(baos);
            String requestId =
                    System.currentTimeMillis() + "-" + idSequence.incrementAndGet();
            dos.writeByte(1);
            dos.writeUTF(method.getName());
            dos.writeUTF(requestId);
            dos.writeByte(args.length);
            for (Object arg : args)
            {
                Serializer.serialize(arg, dos);
            }
            BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(1);
            synchronized (responseQueueMap)
            {
                responseQueueMap.put(requestId, queue);
            }
            outQueue.add(baos.toByteArray());
            // The request has been sent. Now we wait 15 seconds for a response.
            // TODO: make this delay configurable
            Object response = queue.poll(30, TimeUnit.SECONDS);
            synchronized (responseQueueMap)
            {
                responseQueueMap.remove(requestId);
            }
            if (response == null)
                throw new IllegalStateException(
                        "The RPC server didn't respond within 30 seconds.");
            if (response == nullObject)// This is placed in the queue to indicate that
            // the method on the server returned null, in lieu of sticking null into
            // the queue itself as there wouldn't be a way to tell that from the
            // request timing out.
            {
                return null;
            }
            return response;
        }
    }
    
    private class InputThread extends Thread
    {
        public void run()
        {
            try
            {
                while (socket.isConnected() && !socket.isClosed())
                {
                    int packetType = input.read();
                    if (packetType == 1)
                    {
                        processInboundRequest();
                    }
                    else if (packetType == 2)
                    {
                        processInboundResponse();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                socket.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            runOnDisconnect.run();
        }
    }
    
    private class OutputThread extends Thread
    {
        public void run()
        {
            try
            {
                while (socket.isConnected() && !socket.isClosed())
                {
                    byte[] bytes = outQueue.poll(30, TimeUnit.SECONDS);
                    if (bytes == null)
                        continue;
                    output.write(bytes);
                    output.flush();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                socket.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private Map<String, BlockingQueue<Object>> responseQueueMap =
            new HashMap<String, BlockingQueue<Object>>();
    
    private BlockingQueue<byte[]> outQueue = new LinkedBlockingQueue<byte[]>(5000);
    
    private E clientInstance;
    private Class<E> clientInterface;
    private Object serverInterface;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private List<Class> classes;
    private Runnable runOnDisconnect;
    
    public RPCLink(Socket socket, Class<E> clientInterface, Object serverInterface,
            List<Class> classes, Runnable runOnDisconnect) throws IOException
    {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.clientInterface = clientInterface;
        this.serverInterface = serverInterface;
        this.classes = classes;
        this.runOnDisconnect = runOnDisconnect;
        constructClientProxy();
    }
    
    public void processInboundRequest() throws IOException
    {
        final String functionName = input.readUTF();
        final String requestId = input.readUTF();
        int argumentCount = input.read();
        final Object[] args = new Object[argumentCount];
        for (int i = 0; i < args.length; i++)
        {
            args[i] = Serializer.deserialize(input, classes);
        }
        Method[] methods = serverInterface.getClass().getMethods();
        for (final Method method : methods)
        {
            if (method.getName().equals(functionName))
            {
                // TODO: change this to be run in a thread pool executor, as this will
                // result in much faster run times than starting a new thread each time.
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Object response = method.invoke(serverInterface, args);
                            encodeResponse(response, requestId);
                        }
                        catch (Exception e)
                        {
                            RuntimeException e2 =
                                    new RuntimeException(
                                            "Exception while processing inbound RPC call "
                                                + functionName + " for request "
                                                + requestId, e);
                            e2.printStackTrace();
                            throw e2;
                        }
                    }
                }.start();
                return;
            }
        }
        throw new IllegalStateException("No such function " + functionName
            + " with request " + requestId);
    }
    
    private void encodeResponse(Object response, String requestId) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(2);
        dos.writeUTF(requestId);
        dos.writeBoolean(response != null);
        if (response != null)
            Serializer.serialize(response, dos);
    }
    
    public void processInboundResponse() throws IOException
    {
        String requestId = input.readUTF();
        boolean hasResponse = input.readBoolean();
        Object response = null;
        if (hasResponse)
            response = Serializer.deserialize(input, classes);
        if (response == null)
            response = nullObject;
        synchronized (responseQueueMap)
        {
            BlockingQueue<Object> queue = responseQueueMap.get(requestId);
            if (queue != null)
                queue.offer(response);
        }
    }
    
    private void constructClientProxy()
    {
        clientInstance = (E) Proxy.newProxyInstance(null, new Class[]
        {
            clientInterface
        }, new ProxyHandler());
    }
    
    public void close()
    {
        try
        {
            socket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
