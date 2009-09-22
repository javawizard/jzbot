package org.opengroove.jzbot.fact;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.Colors;
import org.opengroove.jzbot.fact.functions.*;

/**
 * A class that can parse factoids. This is where factoid execution starts.
 * 
 * @author Alexander Boyd
 * 
 */
public class FactParser
{
    private static Map<String, Function> functionMap = new HashMap<String, Function>();
    private static Map<Function, String> reverseFunctionMap = new HashMap<Function, String>();
    
    /**
     * Parses the specified factoid into a FactEntity. This fact entity can then
     * be {@link FactEntity#resolve(FactContext) resolved} at any point in the
     * future (and, in fact, resolved multiple times) to actually run this
     * factoid and get its output.<br/><br/>
     * 
     * Currently, the resulting FactEntity is an instance of
     * {@link FunctionReference} that points to the {@link IdentityFunction
     * identity} function, although this behavior should not be relied upon as
     * it may change in the future.<br/><br/>
     * 
     * Parsing a factoid does not cause any side effects, such as changes to
     * local or global variables, to occur. It's only when you actually resolve
     * a factoid that these side effects would occur.
     * 
     * @param factoid
     *            The factoid text to parse
     * @return The parsed factoid
     */
    public static FactEntity parse(String factoid)
    {
        CharStack stack = new CharStack("{{identity||" + factoid + "}}");
        FunctionReference reference = parseFunction(stack);
        if (stack.more())
            /*
             * The only way we can have more here is if they closed the identity
             * function accidentally
             */
            throw new ParseException(stack.at(),
                    "There are more \"}}\" than there are \"{{\"");
        if (reference.getArgumentSequence().length() > 2)
            throw new ParseException(stack.at(),
                    "\"||\" used somewhere in your factoid outside of a function");
        return reference;
    }
    
    /**
     * Parses a CharStack representing a function call into a function
     * reference. Usually, if you're just trying to parse/run a factoid, you'll
     * use {@link #parse(String)} instead. parse(String) interally calls this
     * method with the argument "{{identity||" + factText + "}}" where factText
     * is the text of the factoid.
     * 
     * 
     * @param stack
     *            The CharStack to parse
     * @return The parsed function
     */
    public static FunctionReference parseFunction(CharStack stack)
    {
        // This should be | instead of || to make sure that it's not
        // short-circuit, so that at()-2 would yield the correct result.
        if (stack.next() != '{' | stack.next() != '{')
            throw new ParseException(stack.at() - 2,
                    "Start of function reference must be two open braces but is not");
        Sequence argumentSequence = new Sequence();
        Sequence currentArgument = new Sequence();
        argumentSequence.add(currentArgument);
        Literal currentLiteral = null;
        // Now we parse until we hit one of "%", "{{", "||", or "}}". "%" means
        // a variable reference, so we parse until the next "%", create a
        // literal off of that, and add a reference to the lget command with the
        // argument being the literal. "{{" means the start of another function,
        // which means we go back to just before it, call parseFunction again,
        // and add the resulting function reference to the current argument.
        // "||" means we're on to the next argument, so we add the current
        // argument to the argument sequence and set the current argument to be
        // a new argument. "}}" means we're at the end of the function, so we
        // add the current argument to the argument sequence, create a function
        // reference off of the argument sequence, and return it.
        while (stack.more())
        {
            char c = stack.next();
            if (c == '\\')
            {
                if (currentLiteral == null)
                {
                    currentLiteral = new Literal();
                    currentArgument.add(currentLiteral);
                }
                currentLiteral.append(stack.next());
            }
            else if (c == '%' || c == '$')
            {
                currentLiteral = null;
                StringBuffer l = new StringBuffer();
                char v;
                while ((v = stack.next()) != c)
                {
                    l.append(v);
                }
                currentArgument.add(new VarReference(l.toString(), c == '$'));
            }
            else if (c == '{' && stack.peek() == '{')
            {
                currentLiteral = null;
                stack.back();
                FunctionReference ref = parseFunction(stack);
                currentArgument.add(ref);
            }
            else if (c == '|' && stack.peek() == '|')
            {
                currentLiteral = null;
                stack.next();
                if (currentArgument.length() == 1)
                {
                    /*
                     * If the current argument sequence only has one child,
                     * we'll replace it with its child for efficiency reasons.
                     */
                    argumentSequence.remove(argumentSequence.length() - 1);
                    argumentSequence.add(currentArgument.get(0));
                }
                currentArgument = new Sequence();
                argumentSequence.add(currentArgument);
            }
            else if (c == '}' && stack.peek() == '}')
            {
                currentLiteral = null;
                stack.next();
                if (currentArgument.length() == 1)
                {
                    /*
                     * If the current argument sequence only has one child,
                     * we'll replace it with its child for efficiency reasons.
                     */
                    argumentSequence.remove(argumentSequence.length() - 1);
                    argumentSequence.add(currentArgument.get(0));
                }
                FunctionReference ref = new FunctionReference(argumentSequence);
                return ref;
            }
            else
            {
                if (currentLiteral == null)
                {
                    currentLiteral = new Literal();
                    currentArgument.add(currentLiteral);
                }
                currentLiteral.append(c);
            }
        }
        /*
         * We shouldn't ever get here. If we do, then it means that a function
         * call wasn't closed properly, so we'll throw an exception.
         */
        throw new ParseException(stack.at() - 1,
                "Function call not closed (IE you have "
                        + "more \"{{\" than you have \"}}\"");
    }
    
    public static void install(String name, Function function)
    {
        functionMap.put(name.toLowerCase(), function);
        reverseFunctionMap.put(function, name.toLowerCase());
    }
    
    public static Function getFunction(String name)
    {
        return functionMap.get(name.toLowerCase());
    }
    
    public static String getFunctionName(Function function)
    {
        return reverseFunctionMap.get(function);
    }
    
    static
    {
        installDefaultSet();
        installSpecialSet();
    }
    
    private static void installDefaultSet()
    {
        try
        {
            File factFolder = new File(FactParser.class.getResource(
                    "FactParser.class").toURI()).getParentFile();
            File functionsFolder = new File(factFolder, "functions");
            String[] files = functionsFolder.list();
            for (String file : files)
            {
                try
                {
                    if (file.endsWith("Function.class"))
                    {
                        String className = file.substring(0, file.length()
                                - ".class".length());
                        String functionName = className.substring(0,
                                className.length() - "Function".length())
                                .toLowerCase();
                        System.out.println("Loading function " + functionName);
                        Class<? extends Function> c = (Class<? extends Function>) Class
                                .forName("org.opengroove.jzbot.fact.functions."
                                        + className);
                        install(functionName, c.newInstance());
                    }
                    else
                    {
                        System.out.println("Skipping non-function class "
                                + file);
                    }
                }
                catch (Throwable t)
                {
                    throw new RuntimeException(
                            "Exception while loading function for class file "
                                    + file, t);
                }
            }
        }
        catch (Throwable t)
        {
            throw new RuntimeException(
                    "Error while loading default function set", t);
        }
    }
    
    private static void installSpecialSet()
    {
        install(
                "c",
                new CharCodeSpecial(
                        "c",
                        "\u0003",
                        "Inserts the IRC color change character. Immediately following "
                                + "this should be two digits, which represent the color of text "
                                + "that should show up."));
        install("n", new CharCodeSpecial("n", Colors.NORMAL,
                "Resets any coloring that has been applied in the factoid, so that "
                        + "all succeeding text has no special formatting."));
        install("b", new CharCodeSpecial("b", Colors.BOLD,
                "Inserts the IRC bold character, which causes all following text "
                        + "to be shown as bold."));
        install(
                "i",
                new CharCodeSpecial(
                        "i",
                        Colors.REVERSE,
                        "Inserts the IRC reverse character, which, depending on the client, "
                                + "either reverses the foreground and background colors or shows text"
                                + " as italic."));
        install("u", new CharCodeSpecial("u", Colors.UNDERLINE,
                "Inserts the IRC underline character, which causes all "
                        + "succeeding text to be underlined."));
    }
    
    public static String[] getFunctionNames()
    {
        String[] names = functionMap.keySet().toArray(new String[0]);
        Arrays.sort(names);
        return names;
    }
    
    /**
     * Parses the specified text and then explains it, omitting the default
     * {{identity}} function.
     * 
     * @param factoid
     *            The text of the factoid to explain
     * @return The explanation
     */
    public static String explain(String factoid)
    {
        FunctionReference ref = (FunctionReference) parse(factoid);
        FactEntity entity = ref.getArgumentSequence().get(1);
        return entity.explain(0, 4);
    }
}
