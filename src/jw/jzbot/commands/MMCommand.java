package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.commands.games.MastermindState;
import jw.jzbot.commands.games.RouletteState;


/**
 * A game of mastermind. Uses numbers 1 through 5 as "bead colors". 4 beads by
 * default, I'll probably change that later so you can choose.
 * 
 * ~mm reset resets the game. ~mm 2435 guesses that position, which will result
 * in something like "1 right place, 2 right number wrong place", or
 * "you win! 2435 was the answer."
 * 
 * Games are reset after 10 minutes if unused.
 * 
 * @author Alexander Boyd
 * 
 */
public class MMCommand implements Command
{
    protected static final long TIME_TO_EXPIRE = 0;
    protected static final int numberOfBeads = 4;
    protected static final double numberOfColors = 5.0;
    private static Map<String, MastermindState> stateMap = Collections
            .synchronizedMap(new HashMap<String, MastermindState>());
    
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
                            MastermindState value = stateMap.get(key);
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
        return "mm";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (channel == null)
        {
            JZBot.bot.sendMessage(sender,
                    "You can only use mastermind when a channel is specified.");
            return;
        }
        MastermindState state = stateMap.get(channel);
        if (arguments.equals("info"))
        {
            if (state == null)
                JZBot.bot
                        .sendMessage(pm ? sender : channel,
                                "Info: a game is not in progress. Use ~mm to start one.");
            else
                JZBot.bot.sendMessage(pm ? sender : channel, "Info: "
                        + state.guesses + " guesses so far");
            state.changed = System.currentTimeMillis();
            return;
        }
        if (state == null && !arguments.equals("reset"))
        {
            state = new MastermindState();
            state.changed = System.currentTimeMillis();
            state.guesses = 0;
            for (int i = 0; i < numberOfBeads; i++)
            {
                state.correct
                        .add((int) (1.0 + (Math.random() * numberOfColors)));
            }
            stateMap.put(channel, state);
            JZBot.bot
                    .sendMessage(
                            pm ? sender : channel,
                            "A new game of Mastermind has been started. Positions: "
                                    + numberOfBeads
                                    + ". Numbers 1 through "
                                    + ((int) numberOfColors)
                                    + " are available for guesses. Guess by using ~mm 1234. "
                                    + "Game will reset if unused for 10 minutes.");
            return;
        }
        if (arguments.equals("reset"))
        {
            stateMap.remove(channel);
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "The game has been cleared.");
            return;
        }
        if (arguments.equals("show"))
        {
            if (JZBot.isOp(channel, hostname))
            {
                String answer = "";
                for (int v : state.correct)
                {
                    answer += v;
                }
                JZBot.bot.sendMessage(sender, "The answer is " + answer);
                JZBot.bot.sendMessage(channel, "" + sender
                        + " has seen the answer.");
            }
            else
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "You're not an op here.");
            }
            return;
        }
        /*
         * The arguments are a guess (or at least we'll assume so)
         */
        if (arguments.length() != state.correct.size())
        {
            state.changed = System.currentTimeMillis();
            throw new ResponseException("You guessed " + arguments.length()
                    + " numbers. However, the correct answer has "
                    + state.correct.size()
                    + " number in it. Guess that many numbers.");
        }
        state.changed = System.currentTimeMillis();
        int[] guesses = new int[arguments.length()];
        for (int i = 0; i < arguments.length(); i++)
        {
            guesses[i] = Integer.parseInt(arguments.substring(i, i + 1));
        }
        int rightPosition = 0;
        int rightNumber = 0;
        ArrayList<Integer> correct = new ArrayList<Integer>(state.correct);
        ArrayList<Integer> matched = new ArrayList<Integer>();
        for (int i = 0; i < guesses.length; i++)
        {
            int guess = guesses[i];
            int cValue = state.correct.get(i);
            if (guess == cValue)
            {
                matched.add(i);
                rightPosition++;
                removeOne(correct, guess);
                continue;
            }
        }
        for (int i = 0; i < guesses.length; i++)
        {
            int guess = guesses[i];
            if (matched.contains(i))
                continue;
            int cValue = state.correct.get(i);
            if (correct.contains(guess))
            {
                rightNumber++;
                removeOne(correct, guess);
                continue;
            }
        }
        state.guesses++;
        if (rightPosition == state.correct.size())
        {
            stateMap.remove(channel);
            if (pm)
                JZBot.bot.sendMessage(sender, sender
                        + " won! That was the correct answer.");
            JZBot.bot.sendMessage(channel, sender
                    + " won! That was the correct answer.");
            return;
        }
        JZBot.bot.sendMessage(pm ? sender : channel, sender + ": "
                + rightPosition + " right place, " + rightNumber
                + " right number wrong place. " + state.guesses
                + " guesses so far.");
    }
    
    private void removeOne(ArrayList<Integer> correct, int guess)
    {
        for (int i = 0; i < correct.size(); i++)
        {
            if (correct.get(i) == guess)
            {
                correct.remove(i);
                return;
            }
        }
    }
}
