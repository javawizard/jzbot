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
            // we have an actual property here.
            String valueLineText = null;
            if (line.startsWith("<"))
            {
                // line is a heredoc
                line = line.substring(1);
                if (line.startsWith("<"))
                    line = line.substring(1);
                String heredocTerminator = line.trim();
                String keyContents = "";
                // now we read new lines until we hit one that is equal to the heredoc
                // terminator, exactly
                line = in.readLine();
                while (!line.equals(heredocTerminator))
                {
                    keyContents += line;
                    keyContents += "\n";
                    line = in.readLine();
                }
            }
        }
    }
}
