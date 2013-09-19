package test;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class Test11
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("java.lang:type=OperatingSystem");
        System.out.println(server.getAttribute(name, "TotalSwapSpzsdzsc"));
    }
    
}
