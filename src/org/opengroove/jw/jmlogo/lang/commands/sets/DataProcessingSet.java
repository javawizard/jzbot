package org.opengroove.jw.jmlogo.lang.commands.sets;

import henson.midp.Float11;

import org.opengroove.jw.jmlogo.lang.Command;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.utils.Math2;

public class DataProcessingSet
{
    public static final Command[] set =
        new Command[] { new NamedCommand("list", 2, 256)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                return new ListToken(arguments);
            }
        }, new NamedCommand("wraplist", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                return new ListToken(arguments);
            }
        }, new NamedCommand("word", 2, 256)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < arguments.length; i++)
                {
                    if (!(arguments[i] instanceof WordToken))
                        throw new InterpreterException(
                            "inputs to the word command must themselves be words");
                    buf.append(((WordToken) arguments[i]).getValue());
                }
                return new WordToken(buf.toString());
            }
        }, new NamedCommand("fput", 2, 2)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                validateList(arguments[1]);
                ListToken source = (ListToken) arguments[1];
                return source.fput(arguments[0]);
            }
        }, new NamedCommand("lput", 2, 2)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                validateList(arguments[1]);
                ListToken source = (ListToken) arguments[1];
                return source.lput(arguments[1]);
            }
        }, new NamedCommand("pop", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                validateWord(arguments[0]);
                WordToken token = (WordToken) arguments[0];
                String tokenValue = token.getValue();
                Token list = context.getVariable(tokenValue);
                validateList(list);
                ListToken listToken = (ListToken) list;
                if (listToken.getMembers().length < 1)
                    throw new InterpreterException(
                        "You can't pop from a stack that doesn't have anything on it");
                Token value = listToken.getMembers()[0];
                Token[] newTokens = new Token[listToken.getMembers().length - 1];
                System.arraycopy(listToken.getMembers(), 1, newTokens, 0,
                    newTokens.length);
                ListToken newList = new ListToken(newTokens);
                context.setVariable(tokenValue, newList);
                return value;
            }
        }, new NamedCommand("push", 2, 2)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                validateWord(arguments[0]);
                WordToken token = (WordToken) arguments[0];
                String tokenValue = token.getValue();
                Token list = context.getVariable(tokenValue);
                validateList(list);
                ListToken listToken = (ListToken) list;
                ListToken newToken = listToken.fput(arguments[1]);
                context.setVariable(tokenValue, newToken);
                return null;
            }
        }, new NamedCommand("item", 2, 2)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyWord(arguments[0]);
                WordToken itemPosWord = (WordToken) arguments[0];
                int index = Math2.ri(itemPosWord.getNumeric());
                if (index < 1)
                    throw new InterpreterException("The index passed to the item "
                        + "command can't be less than 1");
                if (arguments[1] instanceof WordToken)
                {
                    WordToken word = (WordToken) arguments[1];
                    String v = word.getValue();
                    if (v.length() < index)
                        throw new InterpreterException("The index " + index
                            + " was greater than the size " + v.length() + " in item");
                    return new WordToken("" + v.charAt(index));
                }
                else
                {
                    ListToken word = (ListToken) arguments[1];
                    Token[] v = word.getMembers();
                    if (v.length < index)
                        throw new InterpreterException("The index " + index
                            + " was greater than the size " + v.length + " in item");
                    return v[index];
                }
            }
        } };
}
