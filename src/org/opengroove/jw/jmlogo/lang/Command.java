package org.opengroove.jw.jmlogo.lang;

/**
 * A logo command. Each primitive command is implemented as a subclass of
 * Command.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class Command

{
    
    public int getMinArgs()
    {
        return getArgumentCount();
    }
    
    public int getMaxArgs()
    {
        return getArgumentCount();
    }
    
    public int getArgumentCount()
    {
        throw new InterpreterException("The command " + getName()
            + " does not override getMinArgs and getMaxArgs, "
            + "or getArgumentCount. It needs to override either "
            + "the first two methods, or the last one.");
    }
    
    /**
     * Returns this command's name as logo programs should use to call the
     * command.
     * 
     * @return
     */
    public abstract String getName();
    
    /**
     * Actually runs this command.
     * 
     * @param context
     */
    public abstract Token run(InterpreterContext context, Token[] arguments);
    
    protected void validateWord(Token token)
    {
        if (!(token instanceof WordToken))
            throw new InterpreterException("Token was not a word");
    }
    
    protected void validateList(Token token)
    {
        if (!(token instanceof ListToken))
            throw new InterpreterException("Token was not a list");
    }
    
    protected void verifyWord(Token token)
    {
        validateWord(token);
    }
    
    protected void verifyList(Token token)
    {
        validateList(token);
    }
    
    public void verifyWords(Token[] tokens, int offset, int limit)
    {
        for (int i = offset; i < offset + limit; i++)
            verifyWord(tokens[i]);
    }
}
