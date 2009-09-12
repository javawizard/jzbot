package org.opengroove.jw.jmlogo.lang.commands.sets;

import java.util.Vector;

import org.opengroove.jw.jmlogo.lang.Command;

public abstract class BaseCommandSet
{
    private Command[] commands = loadCommands0();
    private Vector tempCommandList;
    
    public Command[] getCommands()

    {
        return commands;
    }
    
    private Command[] loadCommands0()
    {
        tempCommandList = new Vector();
        loadCommands();
        Command[] c = new Command[tempCommandList.size()];
        tempCommandList.copyInto(c);
        return c;
    }
    
    protected abstract void loadCommands();
    
    protected void addCommand(Command command)
    {
        tempCommandList.addElement(command);
    }
}
