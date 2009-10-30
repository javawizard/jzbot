package org.opengroove.jzbot.fact.functions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.ConfigVars;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class UrlgetFunction extends Function
{
    
    private static final int MAX_READ_LENGTH = 200 * 1024;
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String urlSpec = arguments.get(0);
        try
        {
            URL url = new URL(urlSpec);
            if (!(url.getProtocol().equals("http") || url.getProtocol().equals("https")))
                throw new RuntimeException("Invalid protocol. Only \"http\" and "
                        + "\"https\" are currently supported, " + "for security reasons.");
            File allowedHostsFile = new File("storage", "allowedhosts.txt");
            if (!allowedHostsFile.exists())
                throw new RuntimeException(
                        "Urlget requests are disabled. Enable them by creating "
                                + "a file called \"allowedhosts.txt\" under the bot's storage "
                                + "folder, and setting its contents to be a regex that describes "
                                + "the allowed hosts.");
            String allowedHostsRegex = StringUtils.readFile(allowedHostsFile).trim();
            if (!url.getHost().matches(allowedHostsRegex))
                throw new RuntimeException("Host " + url.getHost()
                        + " is not allowed. Hosts must match the regex "
                        + allowedHostsRegex);
            /*
             * We've validated the URL. Now we go and get the URL's contents.
             */
            InputStream stream = url.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read = 0;
            while ((read = stream.read(buffer)) != -1)
            {
                if (read > MAX_READ_LENGTH)
                    throw new RuntimeException("Too many characters read (max is "
                            + MAX_READ_LENGTH + ")");
                out.write(buffer, 0, read);
            }
            stream.close();
            out.flush();
            out.close();
            String result = new String(out.toByteArray(), ConfigVars.charset.get());
            return result;
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while getting URL \"" + urlSpec + "\"", e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{urlget||<url>||<mode>||<prefix>}} -- Gets the page at the specified "
                + "url. <url> is the url to get. This currently must only be an http or https "
                + "url. <mode> is the mode to use, which is either \"text\" or \"binary\". "
                + "TODO: binary hexcodes bytes separated by pipes";
    }
    
}
