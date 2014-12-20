package jw.jzbot.protocols.bzflag;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;

public class BZProxy
{
    private static final Object lock = new Object();
    
    private static PrintStream log;
    
    public static class ConnectorPipe extends Thread
    {
        private BZFlagConnector in;
        private BZFlagConnector out;
        private String indicator;
        
        public ConnectorPipe(BZFlagConnector in, BZFlagConnector out, String indicator)
        {
            super();
            this.in = in;
            this.out = out;
            this.indicator = indicator;
        }
        
        public void run()
        {
            try
            {
                while (true)
                {
                    Packet packet = in.receive(Packet.Layer.TCP);
                    synchronized (lock)
                    {
                        log.println(indicator);
                        log.println(dereferenceCode(packet.getType()));
                        log.print("hex     : ");
                        for (byte b : packet.getMessage())
                        {
                            log.print("" + Integer.toHexString((b + 512) % 256) + " ");
                        }
                        log.println();
                        log.print("ascii   : ");
                        for (byte b : packet.getMessage())
                        {
                            log.print("" + (char) ((b + 512) % 256) + "");
                        }
                        log.println();
                        log.println(indicator);
                    }
                    out.send(packet);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        log = new PrintStream(new FileOutputStream("storage/logdump.txt"));
        ServerSocket ss = new ServerSocket(55443);
        System.out.println("waiting");
        Socket s = ss.accept();
        System.out.println("ok");
        BZFlagConnector out = new BZFlagConnector("localhost", 5154);
        s.getOutputStream().write("BZFS0026".getBytes());
        s.getOutputStream().write(out.getSlot());
        BZFlagConnector in = new BZFlagConnector(s, false);
        new ConnectorPipe(in, out, ">>>>").start();
        new ConnectorPipe(out, in, "<<<<").start();
    }
    
    public static String dereferenceCode(int type)
    {
        Class c = BZFlagConnector.class;
        Field[] fields = c.getFields();
        for (Field field : fields)
        {
            try
            {
                boolean isStatic = ((field.getModifiers() & Modifier.STATIC) != 0);
                boolean isInt = field.getType().equals(Integer.TYPE);
                boolean isType;
                if (isStatic && isInt)
                    isType = field.getInt(null) == type;
                else
                    isType = false;
                // System.out.println("" + isStatic + " "
                // + isInt + " " + isType);
                if (isStatic && isInt && isType)
                {
                    return field.getName();
                }
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "Couldn't dereference";
    }
}
