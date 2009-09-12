package org.opengroove.jw.jmlogo.lang;

import java.util.Hashtable;
import java.util.Vector;

import org.opengroove.jw.jmlogo.lang.commands.RepeatCommand;

/**
 * Represents the context in which to execute a logo command. Typically, one
 * InterpreterContext is created for each invocation of a procedure, with it's
 * parent being the interpreter context that represents the procedure that
 * called this one.<br/><br/>
 * 
 * Local variables are local to an interpreter context and it's children. When a
 * variable is accessed, the current context is asked for the variable's value.
 * It checks to see if it has a local variable with that value. If it doesn't,
 * it asks it's parent. It's parent will then perform the same check. If an
 * interpreter context doesn't have a parent (indicating that it represents the
 * topmost method invocation), then it asks the interpreter if it has a global
 * variable by that name. If it doesn't, then null is returned.
 * 
 * @author Alexander Boyd
 * 
 */
public class InterpreterContext
{
    /**
     * Hashtable<String,Variable>
     */
    private Hashtable localVariables = new Hashtable();
    private Interpreter interpreter;
    private InterpreterContext parent;
    private Vector executeInParent = new Vector();
    
    /**
     * Creates a new InterpreterContext.
     * 
     * @param interpreter
     *            The interpreter that this context will be used with
     * @param parent
     *            The parent of this context, or null if this context is to be a
     *            top-level context
     */
    public InterpreterContext(Interpreter interpreter, InterpreterContext parent)
    {
        this.interpreter = interpreter;
        this.parent = parent;
    }
    
    public Variable getVariableHolder(String name)
    {
        Variable v = (Variable) localVariables.get(name);
        if (v != null)
        {
            /*
             * The variable specified has been declared as a local variable.
             * We'll return it. Even if the value of the variable is null, we'll
             * still return it, since it's been localized.
             */
            return v;
        }
        /*
         * The variable isn't defined here. We'll check with our parent, or the
         * interpreter if we don't have a parent.
         */
        if (parent != null)
            return parent.getVariableHolder(name);
        else
            return interpreter.getGlobalVariableHolder(name);
    }
    
    public Token getVariable(String name)
    {
        Variable v = getVariableHolder(name);
        if (v != null)
            return v.getValue();
        return null;
    }
    
    public void createLocal(String name)
    {
        if (localVariables.get(name) != null)
            /*
             * This variable already exists as a local. We don't need to do
             * anything.
             */
            return;
        localVariables.put(name, new Variable(null, this));
    }
    
    /**
     * Short for createLocal(name);setVariable(name,value);
     * 
     * @param name
     * @param value
     */
    public void createLocalVariable(String name, Token value)
    {
        createLocal(name);
        getVariableHolder(name).setValue(value);
    }
    
    public void setVariable(String name, Token value)
    {
        Variable v = getVariableHolder(name);
        if (v == null)
        {
            v = interpreter.createGlobalVariable(name);
        }
        v.setValue(value);
    }
    
    /**
     * Gets the next string to execute within the parent, and removes it.
     * Typically, this will only be called by the interpreter itself.
     * 
     * @return
     */
    public ListToken getNextExecuteInParent()
    {
        if (executeInParent.size() == 0)
            return null;
        ListToken token = (ListToken) executeInParent.elementAt(executeInParent.size() - 1);
        executeInParent.removeElementAt(executeInParent.size() - 1);
        return token;
    }
    
    public boolean hasExecuteInParent()
    {
        return executeInParent.size() > 0;
    }
    
    /**
     * This should only be called from within the run method of a Command, and
     * indicates that when the command returns, if it doesn't output a value,
     * then the instructions indicated by the list token specified should be
     * executed in the context of it's invoker. This is useful for commands that
     * execute an instruction list, since outputting the output from the
     * instructions wouldn't really help, since the executer of the command
     * wouldn't output it's value.<br/><br/>
     * 
     * Check out {@link RepeatCommand} for an example of how to use this.
     * 
     * @param executeInParent
     */
    public void executeInParent(ListToken executeInParent)
    {
        this.executeInParent.insertElementAt(executeInParent, 0);
    }
    
    public Interpreter getInterpreter()
    {
        return interpreter;
    }
    
    public InterpreterContext getParent()
    {
        return parent;
    }
    
    /**
     * Erases the variable by the given name.<br/><br/>
     * 
     * This erases the closest version of that variable to this context. For
     * example, if this context has a local variable by the name specified, and
     * it's parent also has a local variable by the name specified, then this
     * context's local variable will be erased but not it's parent's.
     * 
     * @param name
     *            The name of the variable to erase
     */
    public void eraseVariable(String name)
    {
        Variable v = getVariableHolder(name);
        if (v == null)
        {
            /*
             * The variable doesn't exist, so it's already erased.
             */
            return;
        }
        if (v.getContext() != null)
            v.getContext().localVariables.remove(name);
        else
            interpreter.eraseGlobalVariable(name);
    }
    
    boolean requestedToStop = false;
    Token outputToken = null;
    
    /**
     * True if this interpreter context has been requested to stop.
     * 
     * @return
     */
    public boolean isRequestedToStop()
    {
        return requestedToStop;
    }
    
    public Token getOutputToken()
    {
        return outputToken;
    }
    
    /**
     * Outputs the specified token from the currently running procedure. This
     * can only be called from within the run method on a Command, and will
     * cause the procedure invoking the command to terminate immediately after
     * the command's run method returns, and the procedure will return the
     * outputted value.
     * 
     * @param token
     */
    public void output(Token token)
    {
        this.outputToken = token;
    }
    
    /**
     * Causes the procedure that called the command invoking this to stop
     * immediately, without any output. This is equivalent to the logo "stop"
     * command, and in fact the JMLogo stop command calls this method to do it's
     * work.
     */
    public void stop()
    {
        requestedToStop = true;
    }
    
}
