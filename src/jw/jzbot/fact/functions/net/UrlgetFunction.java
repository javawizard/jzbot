package jw.jzbot.fact.functions.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import jw.jzbot.ConfigVars;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class UrlgetFunction extends Function
{
    
    private static final int MAX_READ_LENGTH = 200 * 1024;
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
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
        return "Syntax: {{urlget||<url>||<method>||<data>}} -- Gets the page at the specified "
                + "url. <url> is the url to get. This currently must only be an http or https "
                + "url. <method> and <data> are both optional, but <method> is required "
                + "if <data> is to be used. <method> is the HTTP method to use, which "
                + "defaults to GET. <data> is the request data to send to the server.\n"
                + "Currently, if <method> is POST and <data> is not empty, an additional "
                + "header, \"Content-Type\", will be sent along with the request with a "
                + "value of \"application/x-www-form-urlencoded\". This will most "
                + "likely changed in the future.";
    }
    
}
