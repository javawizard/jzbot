package org.opengroove.jzbot.fact.functions;

import java.io.File;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class ListresourcesFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return StringUtils.delimited(new File("resources").list(), "/");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{listresources}} -- Returns a forward-slash-separated list of resources "
                + "available to the bot. See {{getresource}} for information on what a resource is.";
    }
    
}
