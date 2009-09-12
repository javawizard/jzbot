package org.opengroove.jw.jmlogo.lang.commands;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.Result;
import org.opengroove.jw.jmlogo.lang.StringStream;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.TokenIterator;
import org.opengroove.jw.jmlogo.lang.Variable;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class RepeatCommand extends Command
{
    
    public int getArgumentCount()
    {
        return 2;
    }
    
    public String getName()
    {
        return "repeat";
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        validateWord(arguments[0]);
        validateList(arguments[1]);
        WordToken wordCount = (WordToken) arguments[0];
        ListToken instructions = (ListToken) arguments[1];
        int repeatCount = (int) wordCount.getLongStrict();
        if (repeatCount == 0)
            return null;
        WordToken previousRepeatCount =
            (WordToken) context.getVariable("_internal_repcount");
        Variable prcHolder = context.getVariableHolder("_internal_repcount");
        if (prcHolder != null && prcHolder.getContext() != context)
            /*
             * repcount has been declared, but scoped to another procedure that
             * called this one, so we'll act as if it didn't exist.
             */
            previousRepeatCount = null;
        for (int i = 1; i <= repeatCount; i++)
        {
            context.createLocalVariable("_internal_repcount", new WordToken(i));
            Result repeatResult =
                context.getInterpreter().evaluate(new TokenIterator(instructions),
                    context);
            if (repeatResult != null)
            {
                if (repeatResult.getType() == Result.TYPE_IN_LINE)
                    throw new InterpreterException("You don't say what to do with "
                        + context.getInterpreter().toReadable(repeatResult.getValue(),
                            64));
                else if (repeatResult.getType() == Result.TYPE_OUTPUT)
                {
                    context.output(repeatResult.getValue());
                    return null;
                }
                else if (repeatResult.getType() == Result.TYPE_STOP)
                {
                    context.stop();
                    return null;
                }
            }
        }
        if (previousRepeatCount != null)
        {
            context.setVariable("_internal_repcount", previousRepeatCount);
        }
        else
        {
            context.eraseVariable("_internal_repcount");
        }
        return null;
    }
}
