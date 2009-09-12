package org.opengroove.jw.jmlogo.lang.commands.sets;

import java.util.Random;

import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class EntropyCommandSet extends BaseCommandSet
{
    public static final EntropyCommandSet set = new EntropyCommandSet();
    
    private static final Random random = new Random();
    
    protected void loadCommands()
    {
        addCommand(new NamedCommand("random", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyWord(arguments[0]);
                WordToken word = (WordToken) arguments[0];
                int max = (int) word.getNumeric();
                double rvar = random.nextInt(max);
                return new WordToken(rvar);
            }
        });
        addCommand(new NamedCommand("pick", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyList(arguments[0]);
                ListToken t = (ListToken) arguments[0];
                if (t.getMembers().length < 1)
                    throw new InterpreterException(
                        "Pick needs a list that has at least one element");
                int v = random.nextInt(t.getMembers().length);
                return t.getMembers()[v];
            }
        });
    }
}
