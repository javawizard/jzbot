package org.opengroove.jzbot.fact;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            else if (c == '%')
            {
                currentLiteral = null;
                StringBuffer l = new StringBuffer();
                char v;
                while ((v = stack.next()) != '%')
                {
                    l.append(v);
                }
                currentArgument.add(new VarReference(l.toString()));
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
    
    public static void install(Function function)
    {
        functionMap.put(function.getName().toLowerCase(), function);
    }
    
    public static Function getFunction(String name)
    {
        return functionMap.get(name.toLowerCase());
    }
    
    static
    {
        installDefaultSet();
    }
    
    private static void installDefaultSet()
    {
        install(new ActionFunction());
        install(new CancelFunction());
        install(new DateformatFunction());
        install(new DeleteFunction());
        install(new ErrorFunction());
        install(new EvalFunction());
        install(new FirstvarFunction());
        install(new FormatFunction());
        install(new FutureFunction());
        install(new GetFunction());
        install(new IdentityFunction());
        install(new IfeqFunction());
        install(new IfFunction());
        install(new IfjoinedFunction());
        install(new IfneqFunction());
        install(new IgnoreFunction());
        install(new ImportFunction());
        install(new IsfutureFunction());
        install(new IsopFunction());
        install(new KickFunction());
        install(new LgetFunction());
        install(new LgvarsFunction());
        install(new LlvarsFunction());
        install(new LsetFunction());
        install(new MatchFunction());
        install(new ModeFunction());
        install(new NumberlistFunction());
        install(new PadFunction());
        install(new RadixFunction());
        install(new RandomFunction());
        install(new RandomintFunction());
        install(new ReplaceFunction());
        install(new RunFunction());
        install(new SendactionFunction());
        install(new SendmessageFunction());
        install(new SetFunction());
        install(new SplitFunction());
        install(new TimemsFunction());
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
