package jw.jzbot.commands;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactQuota;
import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.output.StringSink;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public class ExecCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "exec";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        sender.verifySuperop();
        long startMillis = System.currentTimeMillis();
        FactEntity entity = FactParser.parse(arguments, "<exec>");
        FactContext context = new FactContext();
        context.setServer(server);
        context.setChannel(channel);
        // FIXME: we need to have a senderserver or something on the context
        context.setSender(sender);
        context.setSelf(JZBot.getServer(server).getConnection().getNick());
        context.setQuota(new FactQuota());
        context.setSource(source);
        Map<String, String> vars = new HashMap<String, String>();
        // FIXME: move these vars into a method that sets them all, since they're all set
        // in like 3 different places (running a fact, running a future fact, and exec'ing
        // a fact here)
        vars.put("channel", channel);
        vars.put("server", server);
        vars.put("0", sender.nick());
        vars.put("who", sender.nick());
        // TODO: add a senderserver var or something, or just have that as a function
        vars.put("source", pm ? sender.nick() : channel);
        vars.put("self", context.getSelf());
        context.setLocalVars(vars);
        context.setGlobalVars(JZBot.globalVariables);
        long parsedMillis = System.currentTimeMillis();
        StringSink sink = new StringSink();
        entity.resolve(sink, context);
        String result = sink.toString();
        long finishedMillis = System.currentTimeMillis();
        System.out.println("<exec>: Parsed in " + (parsedMillis - startMillis)
            + " ms, ran in " + (finishedMillis - parsedMillis) + " ms");
        if (result.equals(""))
            result = "(no result)";
        source.sendMessage(result);
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
