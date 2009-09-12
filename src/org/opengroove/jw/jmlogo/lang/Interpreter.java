package org.opengroove.jw.jmlogo.lang;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.opengroove.jw.jmlogo.LogoScreen;
import org.opengroove.jw.jmlogo.lang.commands.*;
import org.opengroove.jw.jmlogo.lang.commands.math.DifferenceCommand;
import org.opengroove.jw.jmlogo.lang.commands.math.ProductCommand;
import org.opengroove.jw.jmlogo.lang.commands.math.QuotientCommand;
import org.opengroove.jw.jmlogo.lang.commands.math.SumCommand;
import org.opengroove.jw.jmlogo.lang.commands.sets.BlockSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.DataProcessingSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.DataProcessingSet2;
import org.opengroove.jw.jmlogo.lang.commands.sets.EntropyCommandSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.InterpreterSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.LogicSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.MathSet;
import org.opengroove.jw.jmlogo.lang.commands.sets.PredicateDataSet;
import org.opengroove.jw.jmlogo.lang.commands.turtle.*;
import org.opengroove.jw.jmlogo.lang.io.InterpreterOutputSink;

/**
 * A logo programming language interpreter.<br/>
 * <br/>
 * 
 * This interpreter is currently not thread-safe.<br/>
 * <br/>
 * 
 * Here's an example of how to use the interpreter to calculate the sum of 2, 3,
 * and 4, and the product of 4 and 5:<br/>
 * 
 * <pre>
 * // one time setup, only do this once per interpreter
 * Interpreter interpreter = new Interpreter();
 * interpreter.installDefaultCommands();
 * // this is done once for any given string that is to be interpreted.
 * // the token can be stored and re-used when executing the command in the future.
 * StringStream ss = new StringStream(&quot;[print (sum 2 3 4) print product 4 5]&quot;);
 * ListToken token = interpreter.parseToList(ss);
 * // this needs to be done once every time the command is executed.
 * InterpreterContext context = new InterpreterContext(interpreter, null);
 * TokenIterator iterator = new TokenIterator(token);
 * Result result = interpreter.evaluate(iterator, context);
 * </pre>
 * 
 * <br/>
 * 
 * <tt>result</tt> can usually be discarded in the example above. It serves to
 * return a value if the command outputted something, or to indicate that the
 * command stopped if it did so. For example, the command [print sum 2 3 4]
 * would return null as the result, the command [sum 2 3 4] would output a
 * result with it's type being TYPE_IN_LINE and it's value being "9", and the
 * command [output sum 2 3 4] would output a result with it's type being
 * TYPE_OUTPUT and it's value being "9".
 * 
 * &#064;author Alexander Boyd
 * 
 */
public class Interpreter
{
    private static final String LIST_DELIMITERS = "() []\n\r\t";
    private static final String WORD_TERMINATORS = " []()\n\r\t";
    private static final String WHITE_SPACE = " \n\r\t";
    private static final String SINGLETON_LIST_COMPONENTS = "()";
    private static final Command EMPTY_COMMAND = new NamedCommand("", 0, 0)
    {
        
        public Token run(InterpreterContext context, Token[] arguments)
        {
            return null;
        }
    };
    /**
     * Hashtable<String,Variable>
     */
    private Hashtable globals = new Hashtable();
    private InterpreterOutputSink outputSink;
    /**
     * Hashtable<String,Command>
     */
    private Hashtable commands = new Hashtable();
    
    public Interpreter()
    {
        
    }
    
    /**
     * Creates a procedure that uses the inputs and name specified by
     * <tt>line</tt> (which can start with "to" or ".macro" but doesn't have to)
     * and the contents specified by <tt>contents</tt>. if <tt>isMacro</tt> is
     * true, then any output provided by the procedure will be executed in the
     * context of the caller of this command. Otherwise, output will function as
     * normal.<br/>
     * <br/>
     * 
     * This method doesn't actually make the procedure available to the
     * interpreter. Use {@link #addCommand(Command)} to actually add the
     * command, or use {@link #define(String, String, boolean)} in place of this
     * method to create the procedure and add it at the same time.
     * 
     * @param line
     *            The initial line of the definition. This is the line that has
     *            "to" or ".macro" on it, although it can be stripped off before
     *            it's passed to this method. The first word should be the name
     *            of the new procedure to create, and each subsequent word
     *            (which can start with a : but doesn't have to) denotes an
     *            argument to the procedure. Variable amounts of arguments are
     *            not allowed right now. Redefining a command that already
     *            exists is not allowed (the erase command should be used first
     *            to get rid of the command, then a command can be redefined).
     * @param contents
     *            The contents of the procedure. It can contain newlines.
     * @param isMacro
     *            True if this function is to be defined as with ".macro", false
     *            if this function is to be defined as with "to".
     * @return
     */
    public Procedure createProcedure(String line, String contents, boolean isMacro)
    {
        String[] lineTokens = split(line, " ");
        Vector argnamesVector = new Vector();
        String name = null;
        for (int i = 0; i < lineTokens.length; i++)
        {
            if (i == 0 && lineTokens[0].equalsIgnoreCase("to"))
                continue;
            if (lineTokens[i].equals(""))
                continue;
            if (name == null)
            {
                name = lineTokens[i];
                continue;
            }
            String varname = lineTokens[i];
            if (varname.startsWith(":"))
                varname = varname.substring(1);
            argnamesVector.addElement(varname);
        }
        if (name == null)
            throw new InterpreterException(
                "Invalid line specified; procedure name is not present");
        String[] argnames = new String[argnamesVector.size()];
        argnamesVector.copyInto(argnames);
        ListToken instructions = parseToList(new StringStream("[" + contents + "]"));
        return new Procedure(name, argnames, instructions, isMacro);
    }
    
    public void addCommand(Command command)
    {
        String name = command.getName().toLowerCase().trim();
        System.out.println("adding command " + name);
        commands.put(name, command);
    }
    
    public void define(String line, String contents, boolean isMacro)
    {
        System.out.println("defining with line " + line);
        addCommand(createProcedure(line, contents, isMacro));
    }
    
    /**
     * Interprets the LOGO commands specified. This is what you call to get the
     * interpreter to actually do stuff. Here's an example of how to use it to
     * print the sum of 2 and 3, using the sum command:<br/>
     * 
     * <pre>
     * Interpreter interpreter = new Interpreter();
     * ListToken token = interpreter.parseToList(&quot;[print sum 2 3]&quot;);
     * TokenIterator iterator = new TokenIterator(token);
     * InterpreterContext context = new InterpreterContext(interpreter, null);
     * interpreter.evaluate(iterator, context);
     * </pre>
     * 
     * <br/>
     * An interpreter context represents a given procedure. This means that
     * you'll typically want to create a new interpreter context for each time
     * you evaluate something, or subsequent evaluations will act like they are
     * in the same procedure. For example, local variables are stored on the
     * context, so creating a new one will clear local variables, which is what
     * should usually happen.<br/>
     * <br/>
     * 
     * You'll always want to create a new token iterator, but you can re-use the
     * list token without problems.
     * 
     * 
     * @param iterator
     *            The commands to execute, in the form of a TokenIterator
     * @param context
     *            The context in which to execute
     * @return The result of the execution. This is usually null, but if the
     *         execution is a function that outputs (for example, executing
     *         "[sum 2 3]" instead of "[print sum 2 3]"), then it will contain
     *         the output. It will also indicate if the instructions called STOP
     *         or OUTPUT, and if they called OUTPUT, the value that was
     *         outputted.
     */
    public Result evaluate(TokenIterator iterator, InterpreterContext context)
    {
        /*
         * We loop until we have a value, an output, a stop, or there are no
         * more tokens left.
         * 
         * If what we have is a value (IE a list token, or a word token starting
         * with " or : (which resolves to a var) or a number), then we return it
         * as TYPE_IN_LINE. Else if what we have is a command, then we run it,
         * calling evaluate to get each of the arguments to the command. If
         * these arguments aren't TYPE_IN_LINE, then we throw a no output
         * exception. We then run the command. If the command has no output and
         * if the command set the stop flag, then we return TYPE_STOP. If the
         * command has no output and if the command has set an output value,
         * then we return TYPE_OUTPUT. If the command doesn't output but none of
         * the previous statements is true, then we continue with token parsing.
         * If the command does output, and it's a procedure, then we return the
         * value it returned. If the command does output, and it's a macro, then
         * we replace it's declaration with the output from the macro, and
         * re-evaluate it.
         * 
         * If what we had wasn't a command, then we issue a "I don't know how to
         * ..." statement in the form of an InterpreterException.
         */
        Token token;
        while ((token = iterator.read()) != null)
        {
            if (token instanceof ListToken)
            {
                /*
                 * This has to be a value, since lists can't directly be
                 * executed. We'll return it.
                 */
                return new Result(token, Result.TYPE_IN_LINE);
            }
            /*
             * This is a word token. We'll cast it to such and get it's value
             * into a string.
             */
            WordToken wordToken = (WordToken) token;
            String tokenValue = wordToken.getValue();
            if (tokenValue.startsWith("\""))
            {
                /*
                 * This token is a literal word. We'll return it.
                 */
                return new Result(new WordToken(tokenValue.substring(1)),
                    Result.TYPE_IN_LINE);
            }
            if (tokenValue.startsWith(":"))
            {
                /*
                 * This token is a reference to a variable. We'll get it's value
                 * and return it.
                 */
                Token var = context.getVariable(tokenValue.substring(1));
                if (var == null)
                    throw new InterpreterException("" + tokenValue.substring(1)
                        + " has no value");
                return new Result(var, Result.TYPE_IN_LINE);
            }
            if (isNumeric(tokenValue))
            {
                /*
                 * This token is a number. We'll return the token itself, since
                 * it's a waste of memory to create a new one when we don't need
                 * to. Word tokens are immutable, so we won't have any issues.
                 */
                return new Result(wordToken, Result.TYPE_IN_LINE);
            }
            /*
             * The token is either a command or an erroneous token. We'll check
             * to see if it's a command, and if it is, we'll run it. If it's
             * not, we'll throw an exception.
             */
            if (tokenValue.equals("(") || getCommand(tokenValue) != null)
            {
                /*
                 * This token is a command. We'll begin parsing arguments for
                 * the command by recursively calling this method. We'll then
                 * execute the command. If the command returns a value, then
                 * we'll return it with TYPE_IN_LINE. If it doesn't, and if it
                 * sets stop, then we'll return TYPE_STOP. If it sets output,
                 * then we'll return the output with TYPE_OUTPUT. If none of
                 * these are true, then we'll see if there are any expressions
                 * to be executed in context. If there are, we'll inline-replace
                 * them into the iterator, and continue through this loop. If
                 * there aren't, then we'll just continue.
                 * 
                 * TODO: add support for variable amounts of arguments for the
                 * command. This can be checked for by seeing if the command
                 * name starts with an open paren. If it does but that's all
                 * there is in the word, then we read the next word and concat
                 * it onto this paren. Then we read arguments using this method
                 * until the next argument is a word that is a close paren. Then
                 * we validate that an acceptable number of arguments were
                 * given, and we execute the given instructions.
                 */
                int startParseIndex = iterator.getIndex();
                boolean isParenEnclosed = tokenValue.equals("(");
                if (isParenEnclosed)
                {
                    Token t2 = iterator.read();
                    if (!(t2 instanceof WordToken))
                        throw new InterpreterException(
                            "Too many parens, I have nothing to do with "
                                + toReadable(t2, 64));
                    WordToken wt2 = (WordToken) t2;
                    tokenValue = wt2.getValue();
                }
                Command command = getCommand(tokenValue);
                int minArgs = command.getMinArgs();
                int maxArgs = command.getMaxArgs();
                Vector commandArgs = new Vector();
                for (int i = 0; i < minArgs; i++)
                {
                    Result argumentResult = evaluate(iterator, context);
                    if (argumentResult == null
                        || argumentResult.getType() != Result.TYPE_IN_LINE)
                        throw new InterpreterException(tokenValue
                            + " was expecting an input, but output wasn't provided");
                    /*
                     * We have the value for this argument now
                     */
                    commandArgs.addElement(argumentResult.getValue());
                }
                if (isParenEnclosed)
                {
                    /*
                     * There are parenthesis around this function call. For each
                     * additional argument, up to maxArgs, allowed by this
                     * command, we'll check to see if the current token is a
                     * close parenthesis. If it is, we'll drop out of this loop.
                     * If it isn't, we'll push it back, re-evaluate, and add the
                     * result as an argument.
                     */
                    for (int i = 0; i < maxArgs - minArgs; i++)
                    {
                        Token nextToken = iterator.read();
                        if ((nextToken instanceof WordToken)
                            && ((WordToken) nextToken).getValue().equals(")"))
                        {
                            /*
                             * Close-paren encountered. We've hit the end of the
                             * function call.
                             */
                            break;
                        }
                        else
                        {
                            iterator.rollback();
                            Result argumentResult = evaluate(iterator, context);
                            if (argumentResult == null
                                || argumentResult.getType() != Result.TYPE_IN_LINE)
                                throw new InterpreterException(
                                    tokenValue
                                        + " was expecting an input, but output wasn't provided");
                            /*
                             * We have the value for this argument now
                             */
                            commandArgs.addElement(argumentResult.getValue());
                        }
                    }
                }
                /*
                 * We've collected the arguments for the function. Now we'll
                 * actually call it.
                 */
                Token[] commandArgsArray = new Token[commandArgs.size()];
                commandArgs.copyInto(commandArgsArray);
                Token commandOutput;
                try
                {
                    commandOutput = command.run(context, commandArgsArray);
                }
                catch (InterpreterException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    System.err.println("Error while executing command "
                        + command.getName());
                    throw new InterpreterException(
                        "An internal error occured while interpreting. "
                            + "The command " + command.getName()
                            + " threw an exception while running. "
                            + "The stack trace has been printed to stdout. "
                            + "Contact support@opengroove.org for help.");
                }
                if (commandOutput != null)
                {
                    context.requestedToStop = false;
                    context.outputToken = null;
                    while (context.getNextExecuteInParent() != null)
                        ;
                    /*
                     * We have output. We'll return it. Note that .macro
                     * functions don't return the macro as a value (they execute
                     * as an expression in context instead), so we don't have to
                     * worry about that.
                     */
                    return new Result(commandOutput, Result.TYPE_IN_LINE);
                }
                /*
                 * This command didn't return anything. We'll check for output
                 * and stop now.
                 */
                if (context.isRequestedToStop())
                {
                    context.requestedToStop = false;
                    context.outputToken = null;
                    while (context.getNextExecuteInParent() != null)
                        ;
                    return new Result(null, Result.TYPE_STOP);
                }
                if (context.getOutputToken() != null)
                {
                    context.requestedToStop = false;
                    Result result =
                        new Result(context.getOutputToken(), Result.TYPE_OUTPUT);
                    context.outputToken = null;
                    while (context.getNextExecuteInParent() != null)
                        ;
                    return result;
                }
                /*
                 * A stop wasn't requested, and neither was an output. There was
                 * no return data either. We'll execute context instructions
                 * now.
                 */
                if (context.hasExecuteInParent())
                {
                    Vector parentExecuteGroup = new Vector();
                    ListToken parentExecuteToken;
                    while ((parentExecuteToken = context.getNextExecuteInParent()) != null)
                    {
                        parentExecuteGroup.addElement(parentExecuteToken.getMembers());
                    }
                    ListToken lastToken =
                        (ListToken) parentExecuteGroup.elementAt(parentExecuteGroup
                            .size() - 1);
                    iterator.replaceBackWith(startParseIndex, lastToken.getMembers());
                    for (int i = parentExecuteGroup.size() - 2; i >= 0; i--)
                    {
                        iterator.insert((Token) parentExecuteGroup.elementAt(i));
                    }
                }
                continue;
            }
            /*
             * This token isn't anything coherent, so we'll throw an exception.
             */
            boolean wasCommand = getCommand(tokenValue) != null;
            throw new InterpreterException("2: I don't know how to \"" + tokenValue
                + "\" \nwascommand " + wasCommand + " \ncmdcount " + commands.size());
        }
        /*
         * All tokens were parsed, with no return statement being executed. This
         * means that none of them were a value, and none of them outputted.
         * We'll return null;
         */
        return null;
    }
    
    private boolean isNumeric(String wv)
    {
        try
        {
            Double.parseDouble(wv);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public Command getCommand(String commandName)
    {
        if (commandName.trim().equals(""))
            return EMPTY_COMMAND;
        return (Command) commands.get(commandName.toLowerCase());
    }
    
    /**
     * Parses the specified token into a list. The input should still have
     * brackets surrounding it; the closing bracket for the list will be parsed
     * but no further. If the open bracket is dangling, or if it is missing, an
     * InterpreterException will be thrown.<br/>
     * <br/>
     * 
     * Currently newlines are interpreted the same way as spaces, unless
     * escaped. This essentially means that you don't have to have a tilde at
     * the end of a line to continue a list onto the next line.
     * 
     * @param s
     *            The text to parse. A java.lang.String can be parsed by passing
     *            it into StringStream's constructor, and passing the
     *            StringStream into this method.
     * @return A ListToken that represents the list
     */
    public ListToken parseToList(StringStream s)
    {
        /*
         * Remove any leading whitespace
         */
        removeWhitespace(s);
        if (s.read() != '[')
            throw new InterpreterException(
                "Missing open bracket on list construct. The input "
                    + "to parseToList must start with a [ and end with a ].");
        /*
         * Tokens within a list are either words or lists. If the first
         * character of a token is an open-bracket, then it's a list, and we'll
         * push the open bracket back and call parseToList again to parse out
         * the list. If the first character of a token is a close bracket, then
         * we've arrived at the end of the list, so we'll return the tokens
         * we've constructed. Otherwise, the token is a word, so we'll parse and
         * add it.
         */
        Vector tokens = new Vector();
        while (true)
        {
            removeWhitespace(s);
            char n = s.read();
            if (n == -1)
            {
                throw new InterpreterException(
                    "Missing close bracket on list construct");
            }
            else if (n == ']')
            {
                Token[] tka = new Token[tokens.size()];
                tokens.copyInto(tka);
                return new ListToken(tka);
            }
            else if (n == '[')
            {
                s.rollback();
                tokens.addElement(parseToList(s));
            }
            else if (SINGLETON_LIST_COMPONENTS.indexOf(n) != -1)
            {
                tokens.addElement(new WordToken(new String(new char[] { (char) n })));
            }
            else
            {
                s.rollback();
                tokens.addElement(new WordToken(readListComponent(s)));
            }
        }
    }
    
    private void removeWhitespace(StringStream s)
    {
        while (WHITE_SPACE.indexOf(s.read()) != -1)
            ;
        s.rollback();
    }
    
    /**
     * Reads up until the next delimiter character.
     * 
     * @param s
     * @return
     */
    private String readListComponent(StringStream s)
    {
        StringBuffer buf = new StringBuffer();
        char c;
        while (true)
        {
            c = s.read();
            if (c == -1)
                return buf.toString();
            else if (c == '\\')
                buf.append(resolveEscapedChar(s.read()));
            else if (LIST_DELIMITERS.indexOf(c) != -1)
            {
                s.rollback();
                return buf.toString();
            }
            else
                buf.append(c);
        }
    }
    
    private String resolveEscapedChar(char read)
    {
        if (read == 'n')
            return "\n";
        else if (read == 'r')
            return "\r";
        else if (read == 't')
            return "\t";
        else if (read == '\b')
            return "\b";
        return "" + read;
    }
    
    /**
     * Parses the buffer into a word. Special chars must be escaped. The first
     * special char encountered...
     * 
     * This should not include the leading double quote.
     * 
     * @param s
     * @return
     */
    public WordToken parseToWord(StringStream s)
    {
        throw new RuntimeException("not supported");
    }
    
    public Variable getGlobalVariableHolder(String name)
    {
        return (Variable) globals.get(name);
    }
    
    public Token getGlobalVariable(String name)
    {
        Variable v = getGlobalVariableHolder(name);
        if (v != null)
            return v.getValue();
        return null;
    }
    
    /**
     * Creates a variable, adds it to the list of globals, and returns it. It's
     * value will initially be null, which can really mess things up, so it
     * should be set to a value before anything else is done.<br/>
     * <br/>
     * 
     * If the variable already exists, an IllegalStateException is thrown.
     * 
     * @param name
     * @return
     */
    public Variable createGlobalVariable(String name)
    {
        if (getGlobalVariableHolder(name) != null)
            throw new IllegalStateException();
        Variable v = new Variable(null, null);
        globals.put(name, v);
        return v;
    }
    
    public void eraseGlobalVariable(String name)
    {
        globals.remove(name);
    }
    
    /**
     * Erases the command by the given name. Care should be taken when using
     * this, as this erases all commands, not just procedures.
     * 
     * @param name
     */
    public void eraseCommand(String name)
    {
        System.out.println("removing command " + name);
        commands.remove(name);
    }
    
    public static String[] split(String string, String around)
    {
        Vector v = new Vector();
        int index;
        while ((index = string.indexOf(around)) != -1)
        {
            v.addElement(string.substring(0, index));
            string = string.substring(index + around.length());
        }
        v.addElement(string);
        String[] result = new String[v.size()];
        v.copyInto(result);
        return result;
    }
    
    public String toReadable(Token token, int lengthLimit)
    {
        StringBuffer sb = new StringBuffer();
        toReadable0(token, sb, lengthLimit);
        if (sb.length() >= lengthLimit)
            sb.append("...");
        return sb.toString();
    }
    
    private void toReadable0(Token token, StringBuffer sb, int lengthLimit)
    {
        if (token instanceof WordToken)
        {
            if (sb.length() < lengthLimit)
                sb.append(((WordToken) token).getValue());
        }
        else if (token instanceof ListToken)
        {
            Token[] tokens = ((ListToken) token).getMembers();
            if (sb.length() < lengthLimit)
                sb.append('[');
            for (int i = 0; i < tokens.length; i++)
            {
                if (i != 0 && sb.length() < lengthLimit)
                    sb.append(' ');
                if (sb.length() < lengthLimit)
                    toReadable0(tokens[i], sb, lengthLimit);
            }
        }
        else
            throw new RuntimeException("Invalid token type");
    }
    
    public void installDefaultCommands()
    {
        addCommand(new LocalCommand());
        addCommand(new MakeCommand());
        addCommand(new PrintCommand());
        addCommand(new ProductCommand());
        addCommand(new RepcountCommand());
        addCommand(new RepeatCommand());
        addCommand(new RunCommand());
        addCommand(new SumCommand());
        addCommand(new DifferenceCommand());
        addCommand(new QuotientCommand());
        addCommand(new ListFixedCommand("listtwo", 2));
        addCommand(new ListFixedCommand("listthree", 3));
        addCommand(new ListFixedCommand("listfour", 4));
        addCommands(DataProcessingSet.set);
        addCommands(DataProcessingSet2.set.getCommands());
        addCommands(BlockSet.set);
        addCommands(LogicSet.commands);
        addCommands(MathSet.commands);
        addCommands(EntropyCommandSet.set.getCommands());
        addCommands(PredicateDataSet.set);
        addCommands(InterpreterSet.set.getCommands());
        alias("arctan2", "jmlogo_arctan2");
        alias("arctan", "jmlogo_arctan");
        addTildeCommand();
    }
    
    private void addTildeCommand()
    {
        addCommand(new NamedCommand("~", 0, 0)
        {
            
            public Token run(InterpreterContext context, Token[] arguments)
            {
                return null;
            }
        });
    }
    
    public void installTurtleCommands(LogoScreen screen)
    {
        for (int i = 0; i < TurtleCommandSet.NUMBER_OF_COMMANDS; i++)
        {
            addCommand(new TurtleCommandSet(screen, i));
        }
        alias("forward", "fw");
        alias("forward", "fd");
        alias("right", "rt");
        alias("left", "lt");
        alias("back", "bk");
        alias("hideturtle", "ht");
        alias("showturtle", "st");
        alias("setscreencolor", "setsc");
        alias("setpencolor", "setpc");
        alias("clearscreen", "cs");
    }
    
    /**
     * Creates an alias for the command specified. This can be used to provide
     * multiple names under which a command can be executed.<br/>
     * <br/>
     * 
     * If the original command is later redefined, the alias will continue to
     * hold the old command.
     * 
     * @param command
     *            The command to alias
     * @param alias
     *            The new alias for the command
     */
    public void alias(String command, String alias)
    {
        commands.put(alias, commands.get(command));
    }
    
    public InterpreterOutputSink getOutputSink()
    {
        return outputSink;
    }
    
    public void setOutputSink(InterpreterOutputSink outputSink)
    {
        this.outputSink = outputSink;
    }
    
    public void addCommands(Command[] commands)
    {
        for (int i = 0; i < commands.length; i++)
        {
            addCommand(commands[i]);
        }
    }
    
    public Command[] getCommands()
    {
        Vector commandList = new Vector();
        Enumeration keySet = commands.keys();
        while (keySet.hasMoreElements())
        {
            Object key = keySet.nextElement();
            Command cmd = (Command) commands.get(key);
            System.out.println("key \"" + key + "\" maps to command \"" + cmd.getName()
                + "\"");
            commandList.addElement(cmd);
        }
        Command[] commandArray = new Command[commandList.size()];
        commandList.copyInto(commandArray);
        return commandArray;
    }
}
