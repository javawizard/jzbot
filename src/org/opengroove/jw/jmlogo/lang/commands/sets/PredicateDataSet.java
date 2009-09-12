package org.opengroove.jw.jmlogo.lang.commands.sets;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class PredicateDataSet
{
    public static final Command[] set =
        new Command[] { new NamedCommand("emptyp", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    return new WordToken(((WordToken) arguments[0]).getValue().equals(
                        ""));
                }
                else
                {
                    return new WordToken(
                        ((ListToken) arguments[0]).getMembers().length == 0);
                }
            }
        } };
}
