package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class LocalCommand extends Command
{
    
    public int getArgumentCount()
    {
        // TODO Auto-generated method stub
        return 1;
    }
    
    public String getName()
    {
        // TODO Auto-generated method stub
        return "local";
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateWord(arguments[0]);
        WordToken tk = (WordToken) arguments[0];
        context.createLocal(tk.getValue());
        return null;
    }
}
