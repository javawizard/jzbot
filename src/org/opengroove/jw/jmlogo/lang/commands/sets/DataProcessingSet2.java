package org.opengroove.jw.jmlogo.lang.commands.sets;

import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.NamedCommand;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class DataProcessingSet2 extends BaseCommandSet
{
    public static DataProcessingSet2 set = new DataProcessingSet2();
    
    protected void loadCommands()
    {
        addCommand(new NamedCommand("first", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    WordToken w = (WordToken) arguments[0];
                    if (w.getValue().length() < 1)
                        throw new InterpreterException(
                            "The word specified is empty, so you "
                                + "can't run the first command on it");
                    return new WordToken(w.getValue().substring(0, 1));
                }
                else
                {
                    ListToken t = (ListToken) arguments[0];
                    if (t.getMembers().length < 1)
                        throw new InterpreterException(
                            "The list specified is empty, so you "
                                + "can't run the first command on it");
                    return t.getMembers()[0];
                }
            }
        });
        addCommand(new NamedCommand("butfirst", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    WordToken t = (WordToken) arguments[0];
                    if (t.getValue().length() < 1)
                        throw new InterpreterException(
                            "empty word specified to butfirst");
                    return new WordToken(t.getValue().substring(1));
                }
                else
                {
                    ListToken t = (ListToken) arguments[0];
                    return t.butFirst();
                }
            }
        });
        addCommand(new NamedCommand("last", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    WordToken w = (WordToken) arguments[0];
                    if (w.getValue().length() < 1)
                        throw new InterpreterException(
                            "The word specified is empty, so you "
                                + "can't run the last command on it");
                    return new WordToken(""
                        + w.getValue().charAt(w.getValue().length() - 1));
                }
                else
                {
                    ListToken t = (ListToken) arguments[0];
                    if (t.getMembers().length < 1)
                        throw new InterpreterException(
                            "The list specified is empty, so you "
                                + "can't run the last command on it");
                    return t.getMembers()[t.getMembers().length - 1];
                }
            }
        });
        addCommand(new NamedCommand("butlast", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    WordToken t = (WordToken) arguments[0];
                    if (t.getValue().length() < 1)
                        throw new InterpreterException(
                            "empty word specified to butlast");
                    return new WordToken(t.getValue().substring(0,
                        t.getValue().length() - 1));
                }
                else
                {
                    ListToken t = (ListToken) arguments[0];
                    return t.butLast();
                }
            }
        });
        addCommand(new NamedCommand("count", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                if (arguments[0] instanceof WordToken)
                {
                    WordToken t = (WordToken) arguments[0];
                    return new WordToken(t.getValue().length());
                }
                else
                {
                    ListToken t = (ListToken) arguments[0];
                    return new WordToken(t.getMembers().length);
                }
            }
        });
        addCommand(new NamedCommand("thing", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyWord(arguments[0]);
                WordToken w = (WordToken) arguments[0];
                Token v = context.getVariable(w.getValue());
                if (v == null)
                    throw new InterpreterException("" + w.getValue()
                        + " has no value, passed to the thing command");
                return v;
            }
        });
        addCommand(new NamedCommand("ern", 1, 1000)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyWords(arguments, 0, arguments.length);
                for (int i = 0; i < arguments.length; i++)
                {
                    context.eraseVariable(((WordToken) arguments[i]).getValue());
                }
                return null;
            }
            
        });
        addCommand(new NamedCommand("namep", 1, 1)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyWord(arguments[0]);
                Token v = context.getVariable(((WordToken) arguments[0]).getValue());
                return new WordToken(v != null);
            }
        });
        addCommand(new NamedCommand("memberp", 2, 2)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                verifyList(arguments[1]);
                ListToken list = (ListToken) arguments[1];
                Token[] members = list.getMembers();
                for (int i = 0; i < members.length; i++)
                {
                    if (members[i].equals(arguments[0]))
                        return new WordToken(true);
                }
                return new WordToken(false);
            }
        });
    }
}
