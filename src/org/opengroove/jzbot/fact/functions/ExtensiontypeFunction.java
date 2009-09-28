package org.opengroove.jzbot.fact.functions;

import org.opengroove.jzbot.HttpServer;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ExtensiontypeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String s = arguments.get(0);
        int lastDotIndex = s.lastIndexOf(".");
        if (lastDotIndex != -1)
            s = s.substring(lastDotIndex + 1);
        s = s.toLowerCase();
        String r = HttpServer.theMimeTypes.get(s);
        if (r == null)
            return "";
        return r;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{extensiontype||<name>}} -- Evaluates to the content type of a number "
                + "of known file extensions. If name does not contain any \".\" characters, "
                + "it is taken to be the name of a file extension. If it does contain \".\" "
                + "characters, the extension is taken to be everything after the last \".\""
                + " character.\nFor example, {{extensiontype||txt}} and {{extensiontype||"
                + "myfile.txt}} both evaluate to \"text/plain\", and {{extensiontype||"
                + "gif}} and {{extensiontype||something.gif}} both evaluate to "
                + "\"image/gif\".";
    }
    
}
