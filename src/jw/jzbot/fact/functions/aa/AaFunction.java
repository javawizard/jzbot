package jw.jzbot.fact.functions.aa;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class AaFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        /*
         * We're using reflection since AutoAbacus might not be installed.
         */
        try
        {
            Class solverClass = Class.forName("com.singularsys.aa.SystemSolver");
            Class systemClass = Class.forName("com.singularsys.aa.EquationSystem");
            Class solutionClass = Class.forName("com.singularsys.aa.Solution");
            Object solver = solverClass.getConstructor().newInstance();
            Object system = systemClass.getConstructor().newInstance();
            String[] equations =
                    arguments.resolveString(1).split(arguments.resolveString(0));
            for (String equation : equations)
            {
                if (equation.trim().equals(""))
                    continue;
                systemClass.getMethod("addEquation", String.class).invoke(system, equation);
            }
            System.out.println("System is instance of " + system.getClass().getName());
            Object solution =
                    solverClass.getMethod("solve", systemClass).invoke(solver, system);
            System.out.println("Solution is instance of " + solution.getClass().getName());
            Enumeration e =
                    (Enumeration) solutionClass.getMethod("getVariableNames").invoke(
                            solution);
            String resultString = "";
            while (e.hasMoreElements())
            {
                String name = (String) e.nextElement();
                String value =
                        solutionClass.getMethod("getValue", String.class).invoke(solution,
                                name).toString();
                resultString += " " + name + "=" + value;
            }
            if (resultString.startsWith(" "))
                resultString = resultString.substring(1);
            sink.write(resultString);
        }
        catch (Exception e)
        {
            if (e instanceof InvocationTargetException)
            {
                InvocationTargetException e2 = (InvocationTargetException) e;
                if (e2.getCause().getClass() == Exception.class)
                {
                    /*
                     * This is thrown by AutoAbacus itself to indicate problems with the
                     * equation system
                     */
                    throw new FactoidException("AA: " + e2.getCause().getMessage(), e);
                }
            }
            if (e instanceof ClassNotFoundException)
            {
                throw new FactoidException("AutoAbacus couldn't be located "
                    + "on the classpath. You need to purchase a copy of "
                    + "AutoAbacus and put it on the classpath in order to "
                    + "use the aa function.", e);
            }
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {aa|<regex>|<equations>} -- Splits <equations> "
            + "around the regular expression <regex>, parses each of the "
            + "resulting items as an equation, and then solves the "
            + "equation system made up of all the equations. This function "
            + "then evaluates to a space-separated list of the results, "
            + "where each result is of the form name=value. For example, "
            + "{aa| |a*b=24 a+b=10} would evaluate to \"a=6.0 b=4.0\" "
            + "or \"a=4.0 b=6.0\". This function requires AutoAbacus to be "
            + "on the classpath; AutoAbacus can be purchased from "
            + "http://www.singularsys.com/autoabacus/ .";
    }
    
}
