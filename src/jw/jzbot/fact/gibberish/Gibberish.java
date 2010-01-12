package jw.jzbot.fact.gibberish;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The official reference implementation of the Gibberish programming language. See
 * http://esolangs.org/wiki/gibberish for more info. I hope to merge the language spec
 * from esolangs into this package (or into the jzbot wiki), at which point that spec will
 * become the official one instead of the one at esolangs (but the one at esolangs will be
 * copied from the one here, possibly by a JZBot script that converts some formatting and
 * stuff).
 * 
 * @author Alexander Boyd
 * 
 */
public class Gibberish
{
    /**
     * A debugger that dumps tons of garbage about each instruction as it is interpreted
     * to stdout.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class PrintDebugger implements Debugger
    {
        
        @Override
        public void after(Gibberish interpreter, String toplevelCode, String code,
                int position, char command)
        {
        }
        
        @Override
        public void before(Gibberish interpreter, String toplevelCode, String code,
                int position, char command)
        {
            System.out.println("Command " + command + " with stack (topmost item last): "
                    + interpreter.debugStack());
        }
        
    }
    
    public static interface Input
    {
        public int read() throws IOException;
        
        public String readLine() throws IOException;
    }
    
    public static interface Output
    {
        public void write(String s) throws IOException;
        
        public void writeln(String s) throws IOException;
    }
    
    public static class BufferedReaderInput implements Input
    {
        private BufferedReader reader;
        
        public BufferedReaderInput(BufferedReader reader)
        {
            super();
            this.reader = reader;
        }
        
        @Override
        public int read() throws IOException
        {
            return reader.read();
        }
        
        @Override
        public String readLine() throws IOException
        {
            return reader.readLine();
        }
        
    }
    
    public static class PrintStreamOutput implements Output
    {
        private PrintStream stream;
        
        public PrintStreamOutput(PrintStream stream)
        {
            super();
            this.stream = stream;
        }
        
        @Override
        public void write(String s)
        {
            stream.print(s);
            stream.flush();
        }
        
        @Override
        public void writeln(String s)
        {
            stream.println(s);
            stream.flush();
        }
        
    }
    
    public static interface Debugger
    {
        /**
         * Called just before executing a particular instruction.
         * 
         * @param interpreter
         *            The interpreter
         * @param code
         *            The code
         * @param position
         *            The index of the first (and usually, only) character of the
         *            instruction
         */
        public void before(Gibberish interpreter, String toplevelCode, String code,
                int position, char command);
        
        /**
         * Called just after executing a particular instruction.
         * 
         * @param interpreter
         *            The interpreter
         * @param code
         *            The code
         * @param position
         *            The index of the first (and usually, only) character of the
         *            instruction
         */
        public void after(Gibberish interpreter, String toplevelCode, String code,
                int position, char command);
        
        // public void begin(Gibberish interpreter, String code);
        
        // public void end(Gibberish interpreter, String code, int position);
    }
    
    public Deque<Object> stack = new LinkedBlockingDeque<Object>();
    
    public static MathContext mc = new MathContext(300);
    
    public int instructionSet;
    
    public Input input;
    
    public Output output;
    
    public Debugger debugger;
    
    private String toplevelCode;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException
    {
        if (args.length == 0)
        {
            System.out
                    .println("Usage: gibberish [-debug] <filename> -- Runs the specified file, "
                            + "as gibberish code. Input will be provided via stdin; "
                            + "output will be sent to stdout.");
            return;
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String filename;
        boolean debug;
        if (args.length == 2 && args[0].equals("-debug"))
        {
            debug = true;
            filename = args[1];
        }
        else
        {
            debug = false;
            filename = args[0];
        }
        FileInputStream in = new FileInputStream(filename);
        String code = new Scanner(in).useDelimiter("\\z").next();
        Gibberish interpreter = new Gibberish(new BufferedReaderInput(inputReader),
                new PrintStreamOutput(System.out), debug ? new PrintDebugger() : null);
        interpreter.interpret(code);
    }
    
    /**
     * Creates a new Gibberish interpreter that will read input from the specified Input
     * object and send output to the specified Output object. If <tt>debugger</tt> is not
     * null, debug output will be sent to the specified debugger.<br/><br/>
     * 
     * This does not cause a Gibberish program to be immediately interpreted; you must
     * call {@link #interpret(String)} on this object to interpret some Gibberish code.
     * 
     * @param in
     *            The input to read from
     * @param out
     *            The output to send to
     * @param debugger
     *            The debugger to write debug information to, or null if the caller does
     *            not need debugging information
     */
    public Gibberish(Input in, Output out, Debugger debugger)
    {
        this.input = in;
        this.output = out;
        this.debugger = debugger;
    }
    
    /**
     * Interprets the specified gibberish code. If there is a syntax error in the code, a
     * RuntimeException will be thrown from this method when such a syntax error occurs.
     * 
     * @param code
     */
    public synchronized void interpret(String code)
    {
        this.toplevelCode = code;
        try
        {
            run(code, 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private void run(String code, int codeOffset) throws IOException
    {
        int index = 0;
        while (index < code.length())
        {
            index = process(index, code, codeOffset);
            index += 1;
        }
    }
    
    private int process(int index, String code, int codeOffset) throws IOException
    {
        char c = code.charAt(index);
        if (c == ' ' || c == '\n' || c == '\r' || c == '\t')
            // ignoring whitespace for now
            return index;
        if (debugger != null)
            debugger.before(this, toplevelCode, code, index, c);
        try
        {
            int newIndex = processCommon(c, index, code, codeOffset);
            if (newIndex != -1)
                return newIndex;
            if (instructionSet == 1)
                return processFirst(c, index, code, codeOffset);
            else if (instructionSet == 2)
                return processSecond(c, index, code, codeOffset);
            else if (instructionSet == 3)
                return processThird(c, index, code, codeOffset);
            else
                throw new RuntimeException(
                        "The current instruction set was not 1, 2, or 3, "
                                + "and an instruction-set-specific instruction was executed.");
        }
        finally
        {
            if (debugger != null)
                debugger.after(this, toplevelCode, code, index, c);
        }
    }
    
    /**
     * Dumps a human-readable version of the operand stack, topmost-item first.
     * 
     * @return
     */
    public String debugStack()
    {
        Object[] os = stack.toArray();
        List<Object> nl = new ArrayList<Object>(Arrays.asList(os));
        Collections.reverse(nl);
        os = nl.toArray(new Object[0]);
        StringBuffer result = new StringBuffer();
        for (Object o : os)
        {
            result.append(", ");
            result.append((o instanceof BigDecimal) ? o.toString() : "\"" + o.toString()
                    + "\"");
        }
        if (result.length() == 0)
            return "(empty)";
        else
            return result.toString().substring(2);
    }
    
    private int processFirst(char c, int index, String code, int codeOffset)
            throws IOException
    {
        if (c == 'a')
        {
            stack.push(((BigDecimal) stack.pop()).add((BigDecimal) stack.pop()));
        }
        else if (c == 'c')
        {
            String first = (String) stack.pop();
            String second = (String) stack.pop();
            stack.push(second + first);
        }
        else if (c == 'i')
        {
            stack.push(new BigDecimal((String) stack.pop()));
        }
        else if (c == 'l')
        {
            stack.push(input.readLine());
        }
        else if (c == 'o')
        {
            output.writeln("" + stack.pop());
        }
        else if (c == 'p')
        {
            int pos = ((BigDecimal) stack.pop()).intValue();
            Iterator it = stack.iterator();
            for (int i = 0; i < pos; i++)
                it.next();
            Object o = it.next();
            stack.push(o);
        }
        else if (c == 'q')
        {
            output.write("" + stack.pop());
        }
        else if (c == 't')
        {
            stack.push(((BigDecimal) stack.pop()).toString());
        }
        else if (c == 'u')
        {
            Object o = stack.pop();
            stack.push(o);
            stack.push(o);
        }
        else if (c == 'v')
        {
            stack.pop();
        }
        else
        {
            invalidInstruction(c);
        }
        return index;
    }
    
    private int processSecond(char c, int index, String code, int codeOffset)
            throws IOException
    {
        if (c == 'c')
        {
            String newCode = (String) stack.pop();
            run(newCode, codeOffset + 1);
        }
        else if (c == 'n')
        {
            stack.push((((BigDecimal) stack.pop()).intValue() == 1) ? BigDecimal.ZERO
                    : BigDecimal.ONE);
        }
        else if (c == 'q')
        {
            stack.push(stack.pop().equals(stack.pop()) ? BigDecimal.ONE : BigDecimal.ZERO);
        }
        else
        {
            invalidInstruction(c);
        }
        return index;
    }
    
    private int processThird(char c, int index, String code, int codeOffset)
            throws IOException
    {
        if (c == 'w')
        {
            String loop = (String) stack.pop();
            while (((BigDecimal) stack.pop()).intValue() == 1)
            {
                // This really whacks up the code offset, but we're not using it in the
                // interpreter for now; it's purely for debug purposes.
                run(loop, codeOffset + 1);
            }
        }
        else
        {
            invalidInstruction(c);
        }
        return index;
    }
    
    private int processCommon(char c, int index, String code, int codeOffset)
    {
        if (c == '[')
        {
            index += 1;
            int level = 0;
            StringBuffer result = new StringBuffer();
            while (code.charAt(index) != ']' || level != 0)
            {
                char current = code.charAt(index);
                if (current == '[')
                    level += 1;
                else if (current == ']')
                    level -= 1;
                result.append(current);
                index += 1;
            }
            stack.push(result.toString());
        }
        else if (c >= '0' && c <= '9')
        {
            stack.push(new BigDecimal(c - '0'));
        }
        else if (c >= 'e' && c <= 'g')
        {
            instructionSet = (c - 'e') + 1;
        }
        else if (c == 'j')
        {
            stack.push(new BigDecimal(instructionSet));
        }
        else if (c == 'x')
        {
            instructionSet = ((BigDecimal) stack.pop()).intValue();
            if (instructionSet < 0 || instructionSet > 3)
                throw new RuntimeException("The \"x\" instruction set the "
                        + "instruction-set number to " + instructionSet
                        + ", which isn't valid. It can only be 0, 1, 2, or 3.");
        }
        else if (c == 'z')
        {
            // nothing to do here, since this is nop
        }
        else
            return -1;
        return index;
    }
    
    private void invalidInstruction(char c)
    {
        throw new RuntimeException("The instruction \"" + c
                + "\" is not a valid instruction in instruction set " + instructionSet
                + ".");
    }
    
}
