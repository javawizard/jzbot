package org.opengroove.jw.jmlogo.lang;

/**
 * Represents the result of evaluating an expression.
 * 
 * @author Alexander Boyd
 * 
 */
public class Result
{
    /**
     * The value that this result has. If the type is TYPE_STOP, then this can
     * be null.
     */
    private Token value;
    /**
     * The type of result that this is. TYPE_OUTPUT indicates that the
     * expression contained an output statement, and the value is the value to
     * be output. TYPE_STOP indicates that the expression contained a stop
     * statement. TYPE_IN_LINE indicates that the expression itself evaluated to
     * a value.<br/><br/>
     * 
     * Methods that wish to indicate that an expression had no result whatsoever
     * would return null. Hence, there is no type for an expression that doesn't
     * evaluate to anything.<br/><br/>
     * 
     * Here's a few examples.<br/><br/>
     * 
     * <table border="1">
     * <tr>
     * <th>String to evaluate</th>
     * <th>Resulting type</th>
     * <th>Resulting value</th>
     * </tr>
     * <tr>
     * <td>make "foo [bar] output [something]</td>
     * <td>TYPE_OUTPUT</td>
     * <td>[something]</td>
     * </tr>
     * <tr>
     * <td>make "foo [bar] stop</td>
     * <td>TYPE_STOP</td>
     * <td>&nbsp;</td>
     * </tr>
     * <tr>
     * <td>[foo]</td>
     * <td>TYPE_IN_LINE</td>
     * <td>[foo]</td>
     * </tr>
     * </table><br/>
     * 
     * Also, if we defined a macro that looked like this:<br/><br/>
     * 
     * <tt>.macro foo<br/>
     * output ["bar]<br/>
     * end</tt><br/><br/>
     * 
     * Then evaluating the expression <tt>foo</tt> would result in a type of
     * TYPE_IN_LINE and a value of <tt>"bar</tt>. A call to a command that
     * outputs a value itself will also generate TYPE_IN_LINE. For example, if
     * the macro above was actually a procedure (IE created with to instead of
     * .macro), then the result would be TYPE_IN_LINE with a value of ["bar].
     */
    private int type;
    public static final int TYPE_OUTPUT = 1;
    public static final int TYPE_STOP = 2;
    public static final int TYPE_IN_LINE = 3;
    /**
     * A result type indicating that the interpreter stopped executing commands
     * because it was halted, and so a result is unavailable.
     */
    public static final int TYPE_HALTED = 4;
    
    public Token getValue()
    {
        return value;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setValue(Token value)
    {
        this.value = value;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public Result(Token value, int type)
    {
        super();
        this.value = value;
        this.type = type;
    }
    
    public Result()
    {
        super();
    }
}
