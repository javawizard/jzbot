package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class MakeCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 2;
    }
    
    public String getName()
    {
        return "make";
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateWord(arguments[0]);
        WordToken wtk = (WordToken) arguments[0];
        String name = wtk.getValue();
        context.setVariable(name, arguments[1]);
        return null;
    }
    
}
