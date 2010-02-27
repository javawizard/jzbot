package jw.jzbot.psystem.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.JZBot;
import jw.jzbot.storage.PersistentKey;

public class PluginManager
{
    private static ServerSocket server;
    
    public static final Object lock = new Object();
    
    private static Map<String, String> tempKeysToNames = new HashMap<String, String>();
    
    private static Thread listenThread = new Thread("plugin-server-listener")
    {
        public void run()
        {
            while (JZBot.isRunning)
            {
                try
                {
                    final Socket s = server.accept();
                    new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                processConnection(s);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                try
                                {
                                    s.close();
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    };
    
    public static void start() throws IOException
    {
        int port;
        File requestedPortFile = new File("storage/requested-plugin-port");
        if (requestedPortFile.exists())
        {
            port = Integer.parseInt(StringUtils.readFile(requestedPortFile).trim());
        }
        else
        {
            port = 0;
        }
        server = new ServerSocket(port, 200);
        if (port == 0)
            port = server.getLocalPort();
        StringUtils.writeFile("" + port, new File("storage/plugin-port"));
        listenThread.setDaemon(true);
        listenThread.start();
    }
    
    public static void shutdown()
    {
        System.out.println("Shutting down the plugin server...");
        try
        {
            server.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.println("Unloading plugins...");
    }
    
    private static void processConnection(Socket connection) throws IOException
    {
        connection.setSoTimeout(20 * 1000);
        DataInputStream in = new DataInputStream(connection.getInputStream());
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        String key = in.readUTF();
        String pluginName = null;
        synchronized (lock)
        {
            PersistentKey persistentKey = JZBot.storage.getPluginKey(key);
            if (persistentKey != null)
            {
                pluginName = persistentKey.getName();
            }
            else
            {
                pluginName = tempKeysToNames.get(key);
            }
        }
        if (pluginName == null)
        {
            /*
             * Yes, the number 42 is a reference to THHGTTG. It was the only number I
             * could think up for starting error codes at.
             */
            out.writeInt(42);
            return;
        }
    }
}
