package org.opengroove.jzbot.fact.bf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I don't honestly recall where I got this program, but the majority of it was
 * not written by me. If anyone knows where this came from, send me an email
 * (javawizard@trivergia.com).
 * 
 * @author Alexander Boyd
 * 
 */
public class Bfi
{
    /** Output of the brainf___ programm goes here, default = System.out **/
    public OutputStream ps; // Output stream
    /** Input of the brainf___ programm is read from here, default = System.in **/
    public InputStream is; // Input stream
    
    /** This points to the currently selected memory cell **/
    public int mp; // Memory pointer
    /** This array represents the memory of the brainf___ program **/
    public int[] cell; // Memory
    
    /** This is the index of the char in the code, which will be executed next **/
    private int cp; // Code pointer
    /** The code is stored in this String **/
    private String cmd; // Code
    
    /** This String contains all in the code allowed chars **/
    public final String CHARS = "<>+-[].,";
    
    /** Standard constructor, sets everything to default **/
    public Bfi()
    {
        init(30000);
    }
    
    /**
     * With this constructor you can specify the number of memory cells to
     * provide, I/O uses default
     **/
    public Bfi(int count)
    {
        init(count);
    }
    
    /**
     * This constructor lets you specify the number of memory cells, and the I/O
     * Streams
     **/
    public Bfi(int count, OutputStream p, InputStream i)
    {
        ps = p;
        is = i;
        init(count);
    }
    
    /** Initialises / Resets the interpreter **/
    
    public void init(int cnt)
    {
        cell = new int[cnt];
        mp = 0;
        if (cmd == null)
            cmd = "";
        if (ps == null)
            ps = System.out;
        if (is == null)
            is = System.in;
    }
    
    /** Resets the memory field and the pointers **/
    
    public void reset()
    {
        cell = new int[cell.length];
        mp = 0;
        cp = 0;
    }
    
    /** Set In- / PrintStream **/
    
    public void setInputStream(InputStream ips, OutputStream ops)
    {
        is = ips;
        ps = ops;
    }
    
    /** Usable for running multiple programs in the same environment **/
    
    public void setPointer(int pos)
    {
        mp = pos;
    }
    
    /**
     * Loads brainf___ program, filters every char not in the CHARS constant or
     * after a \ out of the String
     **/
    
    public void setProgram(String prg)
    {
        
        boolean take = false;
        cmd = "";
        for (int i = 0; i < prg.length(); i++)
        {
            if (take)
            {
                cmd += prg.charAt(i);
                take = false;
            }
            else
            {
                if (CHARS.indexOf(prg.charAt(i)) != -1)
                {
                    cmd += prg.charAt(i);
                }
                else if (prg.charAt(i) == '\\')
                {
                    cmd += "\\";
                    take = true;
                }
            }
        }
        
        System.err.println("Parsed program: " + cmd);
        
    }
    
    /** Returns next char **/
    
    public char getChar()
    {
        return getChar(this.cp);
    }
    
    /** Returns char from command string at specified position **/
    
    public char getChar(int nr)
    {
        return cmd.charAt(nr);
    }
    
    /** Get a slice of the programm **/
    
    public String getChars(int start, int end)
    {
        
        String tmp = "";
        for (int i = start - 1; i < end; i++)
        {
            tmp += cmd.charAt(i);
        }
        return tmp;
        
    }
    
    /** Interpret program, set start address **/
    
    public boolean interpret(int spos)
    {
        this.cp = spos;
        return interpret();
    }
    
    /** Start interpretation of programm **/
    
    public boolean interpret()
    {
        
        char inst;
        
        // Fetch next instruction
        try
        {
            inst = getChar();
        }
        catch (Exception e)
        {
            return false;
        }
        
        // Parse instruction
        switch (inst)
        {
            case '<':
                mp = (mp > 0) ? mp - 1 : 0;
                break;
            case '>':
                mp = (mp < cell.length) ? mp + 1 : mp;
                break;
            case '+':
                cell[mp]++;
                break;
            case '-':
                cell[mp]--;
                break;
            case '[':
                if (cell[mp] <= 0)
                    return jumpWhileEnd();
                break;
            case ']':
                return jumpWhileStart();
                // break;
            case '\\':
                try
                {
                    cell[mp] = getChar(cp + 1);
                    cp++;
                }
                catch (Exception e)
                {
                    return false;
                }
                break;
            case '.':
                try
                {
                    ps.write(cell[mp]);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                break;
            case ',':
                try
                {
                    cell[mp] = is.read();
                }
                catch (Exception e)
                {
                    return false;
                }
                break;
        }
        cp++;
        
        return true;
        
    }
    
    /** This method searches the corresponding [ sign **/
    
    public boolean jumpWhileStart()
    {
        
        int level = 0;
        
        for (int i = cp - 1; i >= 0; i--)
        {
            switch (cmd.charAt(i))
            {
                case '[':
                    if (level > 0)
                    {
                        level--;
                    }
                    else
                    {
                        cp = i;
                        return true;
                    }
                    break;
                case ']':
                    level++;
                    break;
            }
        }
        return false;
        
    }
    
    /** This method searches trailing ] sign **/
    
    public boolean jumpWhileEnd()
    {
        
        int level = 0;
        
        for (int i = cp + 1; i < cmd.length(); i++)
        {
            switch (cmd.charAt(i))
            {
                case '[':
                    level++;
                    break;
                case ']':
                    if (level <= 0)
                    {
                        cp = i + 1;
                        return true;
                    }
                    else
                    {
                        level--;
                    }
                    break;
            }
        }
        return false;
        
    }
    
    /** This method goes through the whole code **/
    
    public void start()
    {
        start(0);
    }
    
    /**
     * This method goes through the whole code starting at a certain point in
     * the code
     **/
    
    public void start(int off)
    {
        
        if (interpret(off))
        {
            while (interpret())
            {
            }
        }
        String s1 = null;
        String s2 = null;
        try
        {
            s1 = getChars(cp - 1, cp + 2);
            s2 = getChars(1, cp + 2);
        }
        catch (Exception e)
        {
            if (cp >= cmd.length())
            {
                return;
            }
            else
            {
                throw new RuntimeException(
                        "Unspecified exception while interpreting");
            }
        }
        throw new RuntimeException(
                "Code-positional exception while interpreting, s1=" + s1
                        + ",s2=" + s2 + ",cp=" + cp + ",cmdl=" + cmd.length());
    }
    
    /**
     * Start a simple Hello World program, using the special ascii extension of
     * this interpreter
     **/
    
    public void helloworldExt()
    {
        
        init(100);
        setProgram("\\H.\\a.\\l.\\l.\\o.\\ .\\W.\\e.\\l.\\t.\\!.");
        start();
        
    }
    
    /** Starts a simple Hello World program, in "standard" brainf___ **/
    
    public void helloworld()
    {
        
        init(100);
        setProgram(">+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.[-]>++++++++[<++++>-] <.>+++++++++++[<++++++++>-]<-.--------.+++.------.--------.[-]>++++++++[<++++>- ]<+.[-]++++++++++.");
        start();
        
    }
    
}
