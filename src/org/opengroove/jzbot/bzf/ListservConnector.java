package org.opengroove.jzbot.bzf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ListservConnector
{
    
    private static final String LIST_SERVER_URL = "http://my.bzflag.org/db/?action=LIST&version=BZFS0026&local=1";
    
    public static Server[] getServers() throws IOException
    {
        URL url = new URL(LIST_SERVER_URL);
        InputStream input = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        ArrayList<Server> list = new ArrayList<Server>();
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.trim().equals(""))
                continue;
            Server server = new Server();
            server.parseLine(line.trim());
            list.add(server);
        }
        Server[] servers = list.toArray(new Server[0]);
        Arrays.sort(servers, new Comparator<Server>()
        {
            
            @Override
            public int compare(Server o1, Server o2)
            {
                if (o1.playerCount > o2.playerCount)
                    return -1;
                else if (o2.playerCount > o1.playerCount)
                    return 1;
                else
                    return 0;
            }
        });
        return servers;
    }
}
