package org.opengroove.jw.jmlogo.lang.commands.sets;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Result;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.TokenIterator;
import org.opengroove.jw.jmlogo.lang.WordToken;

/**
 * A set of commands that generally operate on blocks of instructions.<br/>
 * <br/>
 * 
 * This command set includes the following commands:<br/>
 * <br/>
 * 
 * <ul>
 * <li>if</li>
 * <li>ifelse</li>
 * <li>while</li>
 * </ul>
 * 
 * @author Alexander Boyd
 * 
 */
public class BlockSet
{
    public static final Command[] set = new Command[] { new NamedCommand("while", 2, 2)
    {
        
        public Token run(InterpreterContext context, Token[] arguments)
        {
            validateList(arguments[0]);
            validateList(arguments[1]);
            ListToken condition = (ListToken) arguments[0];
            ListToken expression = (ListToken) arguments[1];
            while (true)
            {
                Result conditionResult =
                    context.getInterpreter().evaluate(new TokenIterator(condition),
                        context);
                if (conditionResult.getType() != Result.TYPE_IN_LINE)
                    throw new InterpreterException(
                        "The condition input to a while must evaluate to a value");
                Token conditionToken = conditionResult.getValue();
                validateWord(conditionToken);
                WordToken conditionWord = (WordToken) conditionToken;
                if (conditionWord.getValue().equalsIgnoreCase("false"))
                    break;
                else if (!conditionWord.getValue().equalsIgnoreCase("true"))
                    throw new InterpreterException(
                        "The condition input to a while must evaluate "
                            + "to either \"true or \"false");
                Result expressionResult =
                    context.getInterpreter().evaluate(new TokenIterator(expression),
                        context);
                if (expressionResult != null)
                {
                    if (expressionResult.getType() == Result.TYPE_IN_LINE)
                        throw new InterpreterException("You don't say what to do with "
                            + context.getInterpreter().toReadable(
                                expressionResult.getValue(), 64));
                    else if (expressionResult.getType() == Result.TYPE_OUTPUT)
                    {
                        context.output(expressionResult.getValue());
                        return null;
                    }
                    else if (expressionResult.getType() == Result.TYPE_STOP)
                    {
                        context.stop();
                        return null;
                    }
                }
            }
            return null;
        }
    }, new NamedCommand("ifelse", 3, 3)
    {
        
        public Token run(InterpreterContext context, Token[] arguments)
        {
            validateWord(arguments[0]);
            validateList(arguments[1]);
            validateList(arguments[2]);
            WordToken condition = (WordToken) arguments[0];
            ListToken ifTrue = (ListToken) arguments[1];
            ListToken ifFalse = (ListToken) arguments[2];
            boolean isExpressionTrue;
            if (condition.getValue().equalsIgnoreCase("true"))
                isExpressionTrue = true;
            else if (condition.getValue().equalsIgnoreCase("false"))
                isExpressionTrue = false;
            else
                throw new InterpreterException(
                    "\"true or \"false must be supplied as the condition to an if statement");
            ListToken toExecute = isExpressionTrue ? ifTrue : ifFalse;
            Result expressionResult =
                context.getInterpreter()
                    .evaluate(new TokenIterator(toExecute), context);
            if (expressionResult != null)
            {
                if (expressionResult.getType() == Result.TYPE_IN_LINE)
                    return expressionResult.getValue();
                else if (expressionResult.getType() == Result.TYPE_OUTPUT)
                {
                    context.output(expressionResult.getValue());
                    return null;
                }
                else if (expressionResult.getType() == Result.TYPE_STOP)
                {
                    context.stop();
                    return null;
                }
            }
            return null;
        }
    } };
}
