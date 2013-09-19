package jw.jzbot.fact.functions;

import jw.jzbot.HttpServer;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ExtensiontypeFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String s = arguments.resolveString(0);
        int lastDotIndex = s.lastIndexOf(".");
        if (lastDotIndex != -1)
            s = s.substring(lastDotIndex + 1);
        s = s.toLowerCase();
        String r = HttpServer.theMimeTypes.get(s);
        if(r != null)
            sink.write(r);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {extensiontype|<name>} -- Evaluates to the content type of a number "
                + "of known file extensions. If name does not contain any \".\" characters, "
                + "it is taken to be the name of a file extension. If it does contain \".\" "
                + "characters, the extension is taken to be everything after the last \".\""
                + " character.\nFor example, {extensiontype|txt} and {extensiontype|"
                + "myfile.txt} both evaluate to \"text/plain\", and {extensiontype|"
                + "gif} and {extensiontype|something.gif} both evaluate to "
                + "\"image/gif\".";
    }
    
}
