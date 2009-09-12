package org.opengroove.jw.jmlogo.lang.commands.sets;

import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Token;

public class InterpreterSet extends BaseCommandSet
{
    public static final InterpreterSet set = new InterpreterSet();
    
    protected void loadCommands()
    {
        addCommand(new NamedCommand("output", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                context.output(arguments[0]);
                return null;
            }
        });
        addCommand(new NamedCommand("stop", 0, 0)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                context.stop();
                return null;
            }
        });
        addCommand(new NamedCommand("listmode", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                return null;
            }
        });
    }
}
