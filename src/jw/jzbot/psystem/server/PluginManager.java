package jw.jzbot.psystem.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.JZBot;

public class PluginManager
{
    private static ServerSocket server;
    
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
                            processConnection(s);
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
        listenThread.setDaemon(true);
        listenThread.start();
    }
    
    public static void shutdown()
    {
        System.out.println("Unloading bznetwork...");
    }
    
    private static void processConnection(Socket connection)
    {
        
    }
}
