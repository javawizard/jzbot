package jw.jzbot.commands;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactEntity;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactQuota;
import jw.jzbot.fact.StringSink;

public class ExecCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "exec";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        JZBot.verifySuperop(server, hostname);
        long startMillis = System.currentTimeMillis();
        FactEntity entity = FactParser.parse(arguments, "__internal_exec");
        FactContext context = new FactContext();
        context.setServer(server);
        context.setChannel(channel);
        context.setSender(sender);
        context.setSelf(JZBot.bot.getNick());
        context.setQuota(new FactQuota());
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("channel", channel);
        vars.put("server", server);
        vars.put("0", sender);
        vars.put("who", sender);
        vars.put("source", pm ? sender : channel);
        context.setLocalVars(vars);
        context.setGlobalVars(JZBot.globalVariables);
        long parsedMillis = System.currentTimeMillis();
        StringSink sink = new StringSink();
        entity.resolve(sink, context);
        String result = sink.toString();
        long finishedMillis = System.currentTimeMillis();
        System.out.println("__internal_exec: Parsed in " + (parsedMillis - startMillis)
                + " ms, ran in " + (finishedMillis - parsedMillis) + " ms");
        if (result.equals(""))
            result = "(no result)";
        JZBot.bot.sendMessage(pm ? sender : channel, result);
    }
}
