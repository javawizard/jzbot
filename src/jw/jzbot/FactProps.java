package jw.jzbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public class FactProps extends HashMap<String, String>
{
    public void load(Reader rd) throws IOException
    {
        BufferedReader in = new BufferedReader(rd);
        String line;
        while ((line = in.readLine()) != null)
        {
            if (line.trim().equals(""))
                continue;
            else if (line.startsWith("#"))
                continue;
        }
    }
}
