package org.opengroove.jw.jmlogo.lang;

import org.opengroove.jw.jmlogo.lang.commands.MakeCommand;
import org.opengroove.jw.jmlogo.lang.commands.PrintCommand;
import org.opengroove.jw.jmlogo.lang.commands.RepcountCommand;
import org.opengroove.jw.jmlogo.lang.commands.RepeatCommand;
import org.opengroove.jw.jmlogo.lang.commands.math.SumCommand;

public class TestInterpreter
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String toInterpret = "";
        for (int i = 0; i < args.length; i++)
        {
            if (i != 0)
                toInterpret += " ";
            toInterpret += args[i];
        }
        Interpreter it = new Interpreter();
        it.installDefaultCommands();
        StringStream s = new StringStream("[" + toInterpret + "]");
        ListToken tk = it.parseToList(s);
        InterpreterContext context = new InterpreterContext(it, null);
        Result result = it.evaluate(new TokenIterator(tk), context);
        if (result != null)
        {
            if (result.getType() == Result.TYPE_IN_LINE)
                throw new InterpreterException("I don't know what to do with "
                    + it.toReadable(result.getValue(), 64));
            else if (result.getType() == Result.TYPE_OUTPUT)
                throw new InterpreterException(
                    "You can only use output inside a procedure");
        }
    }
    
}
