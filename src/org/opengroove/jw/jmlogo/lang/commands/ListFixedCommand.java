package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.Token;

/**
 * This command, when created, takes a number, which indicates how many
 * arguments it accepts, and a name for itself. It then accepts that many
 * arguments, and turns them into a list. Once support for variable number of
 * parameters is added to the interpreter, then this will be replaced by the
 * logo standard "list" command.<br/><br/>
 * 
 * For example, this class could be used to add a command by the name of
 * "listthree", which accepts three arguments, and turns them into a list.<br/><br/>
 * 
 * UPDATE: As of 2009.02.10, JMLogo now supports variable numbers of arguments
 * to a function. This command is now obsolete.
 * 
 * @author Alexander Boyd
 * 
 */
public class ListFixedCommand extends Command
{
    private int args;
    
    private String name;
    
    public ListFixedCommand(String name, int args)
    {
        this.name = name;
        this.args = args;
    }
    
    public int getArgumentCount()
    {
        return args;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        return new ListToken(arguments);
    }
    
}
