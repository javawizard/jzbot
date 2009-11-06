package jw.jzbot.commands.d;

import jw.jzbot.Command;

/**
 * A simple game of Tic Tac Toe. One game per channel. The game will be removed
 * after 10 minutes of not being requested or modified.
 * 
 * ~ttt 1 X places an X in the upper-left corner. ~ttt 1 O is similar. ~ttt 3 X
 * is upper-right, ~ttt 7 X is lower-left.
 */
public class TTTCommand implements Command
{
    
    public String getName()
    {
        return "ttt";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
        String arguments)
    {
        // TODO Auto-generated method stub
        
    }
    
}
