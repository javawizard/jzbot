package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class PrintCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 1;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        if (arguments[0] instanceof WordToken)
            System.out.println(((WordToken) arguments[0]).getValue());
        else
        {
            ListToken t = (ListToken) arguments[0];
            Token[] members = t.getMembers();
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < members.length; i++)
            {
                if (i != 0)
                    result.append(" ");
                result.append(context.getInterpreter().toReadable(members[i], 100000));
            }
            System.out.println(result);
        }
        return null;
    }
    
    public String getName()
    {
        return "print";
    }
    
}
