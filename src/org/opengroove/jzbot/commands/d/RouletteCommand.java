package org.opengroove.jzbot.commands.d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.commands.games.RouletteState;

/**
 * This is obsolete, as the roulette command has been re-written as a factpack
 * (specifically "local.games.roulette"). See "factpacks/roulette.jzf".
 * 
 * @author Alexander Boyd
 * 
 */
public class RouletteCommand implements Command
{
    protected static final long TIME_TO_EXPIRE = 1000 * 60 * 5;
    private static Map<String, RouletteState> stateMap = Collections
            .synchronizedMap(new HashMap<String, RouletteState>());
    
    static
    {
        new Thread()
        {
            public void run()
            {
                while (JZBot.isRunning)
                {
                    try
                    {
                        Thread.sleep(30 * 1000);
                        for (String key : new ArrayList<String>(stateMap
                                .keySet()))
                        {
                            RouletteState value = stateMap.get(key);
                            if (value != null)
                            {
                                if ((value.changed + TIME_TO_EXPIRE) < System
                                        .currentTimeMillis())
                                    stateMap.remove(key);
                            }
                        }
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        }.start();
    }
    
    public String getName()
    {
        return "roulette";
    }
    
    public synchronized void run(String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        if (channel == null)
        {
            JZBot.bot.sendMessage(sender,
                    "You can only use roulette when a channel is specified.");
            return;
        }
        RouletteState state = stateMap.get(channel);
        if (state == null)
        {
            state = new RouletteState();
            state.changed = System.currentTimeMillis();
            state.current = 0;
            state.loaded = (int) ((Math.random() * 7.0) + 1.0);
            stateMap.put(channel, state);
        }
        if (arguments.equals("reset"))
        {
            if (JZBot.isOp(channel, hostname))
            {
                stateMap.remove(channel);
                JZBot.bot.sendMessage(pm ? sender : channel, "Roulette reset.");
            }
            else
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "You're not an op here.");
            }
            return;
        }
        if (arguments.equals("show"))
        {
            if (JZBot.isOp(channel, hostname))
            {
                if (state.loaded == 7)
                {
                    JZBot.bot
                            .sendMessage(sender,
                                    "The gun is unloaded. You'll see this after chamber 6.");
                }
                else
                {
                    JZBot.bot.sendMessage(sender, "The loaded chamber is "
                            + state.loaded);
                }
                JZBot.bot.sendMessage(channel, "" + sender
                        + " has seen which chamber is loaded.");
            }
            else
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "You're not an op here.");
            }
            return;
        }
        state.changed = System.currentTimeMillis();
        state.current++;
        String prefix = "" + sender + ": (Chamber " + state.current + " of 6) ";
        if (state.current == state.loaded)
        {
            stateMap.remove(channel);
            JZBot.bot.sendMessage(channel, prefix + "*BANG* You're dead.");
            JZBot.bot.kick(channel, sender, "*BANG* You're dead.");
            JZBot.bot.sendAction(channel, "reloads and spins the chamber");
        }
        else
        {
            JZBot.bot.sendMessage(channel, prefix + "*click*");
            if (state.current == 6)
            {
                stateMap.remove(channel);
                JZBot.bot.sendMessage(channel,
                        "The gun was unloaded. Luckster.");
                JZBot.bot.sendAction(channel, "reloads and spins the chamber");
            }
        }
    }
}
