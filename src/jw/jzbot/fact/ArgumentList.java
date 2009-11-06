package jw.jzbot.fact;

/**
 * A list of arguments. This is primarily used when calling function implementations. An
 * argument list is passed into each such invocation, and the function can use it to
 * obtain the values of certain arguments and, if needed, cause the re-evaluation of
 * certain arguments.<br/><br/>
 * 
 * Arguments are lazily loaded. This means that none of the arguments to a function are
 * actually run until the function requests their value. This allows for functions such as
 * the if function, which selectively run arguments based on the value of other arguments.
 * Throughout this class's documentation, the act of actually running an argument is
 * referred to as "resolving" the argument.<br/><br/>
 * 
 * Argument lists can be of one of two types: <b>normal lists</b> and <b>delegated
 * lists</b>. Normal lists contain a reference to the parsed representation of each
 * argument. Delegated lists contain a reference to another argument list, along with an
 * offset and a length, and the delegated list acts as a view of the specified subset of
 * the list it delegates to.
 * 
 * @author Alexander Boyd
 * 
 */
public class ArgumentList
{
    /**
     * A list of arguments that this argument list delegates to, which is only present if
     * we're a delegated list.
     */
    private ArgumentList delegate;
    /**
     * If this is a delegated list, then this is the offset into {@link #delegate} that
     * this list should read.
     */
    private int offset;
    /**
     * If this is a delegated list, then this is the number of items that this list should
     * be able to read from the list that it delegates to.
     */
    private int length;
    /**
     * If this is a normal list, then this is a Sequence object that represents the list
     * of entities that correspond to each item in the list.
     */
    private Sequence sequence;
    /**
     * If this is a normal list, then this represents the values that have already been
     * resolved.
     */
    private String[] resolved;
    /**
     * The context that arguments should be resolved in.
     */
    private FactContext context;
    
    /**
     * Creates a normal argument list from the specified sequence and context. Each item
     * in the sequence will become an item in the argument list, and each of those items
     * will be resolved when {@link #get(int)} is called on the new list.
     * 
     * @param sequence
     *            The sequence that this argument list should take its values from
     * @param context
     *            The context in which to resolve members of <tt>sequence</tt> as they are
     *            needed for use
     */
    public ArgumentList(Sequence sequence, FactContext context)
    {
        this.context = context;
        this.sequence = sequence;
        this.resolved = new String[sequence.length()];
    }
    
    /**
     * Creates a delegated argument list that reads from <tt>delegate</tt>. The new list
     * will represent a view of <tt>delegate</tt> starting at <tt>offset</tt> and reading
     * for <tt>length</tt> items.<br/>
     * 
     * Normally, a delegated list is created by calling {@link #subList(int)} or
     * {@link #subList(int, int)} on the list that a delegate should be created for.
     * 
     * @param delegate
     *            The list to delegate to
     * @param offset
     *            The offset that this list should start reading at
     * @param length
     *            The number of items that this list can read into <tt>delegate</tt>
     * @param context
     *            The context for this list. This isn't currently used by delegate lists,
     *            but it might be used in the future. This should always be the same as
     *            <tt>delegate</tt>'s context.
     */
    private ArgumentList(ArgumentList delegate, int offset, int length, FactContext context)
    {
        this.context = context;
        this.delegate = delegate;
        this.offset = offset;
        this.length = length;
    }
    
    /**
     * Same as <tt>get()</tt>, but the argument is resolved again even if it has already
     * been resolved before. Functions like {{split}} use this for the <action> argument
     * to cause it to be resolved once for each substring in the string to split.
     * 
     * @param index
     *            The index of the item to resolve
     * @return The value that the newly-resolved item evaluated to
     */
    public String resolve(int index)
    {
        if (delegate != null)
        {
            return delegate.resolve(index + offset);
        }
        else
        {
            return sequence.get(index).resolve(context);
        }
    }
    
    /**
     * Returns a Deferred object representing the value at the specified index. This does
     * not cause the value to be resolved immediately; the value will only be resolved
     * once a method is called on the returned Deferred object.
     * 
     * @param index
     *            The index to get a Deferred for
     * @return A new Deferred object for the specified index
     */
    public Deferred getDeferred(int index)
    {
        return new Deferred(this, index);
    }
    
    /**
     * Gets the argument at the specified index. If this argument has not actually been
     * "run" to find out what its value should be, it is run, and the value is stored so
     * that future calls to <tt>get()</tt> will not result in the argument being "run"
     * again.
     * 
     * @param index
     *            The index of the argument. Indexes start at 0.
     * @return The result of running the argument at the specified index
     */
    public String get(int index)
    {
        if (delegate != null)
        {
            return delegate.get(index + offset);
        }
        else
        {
            if (resolved[index] == null)
            {
                resolved[index] = sequence.get(index).resolve(context);
            }
            return resolved[index];
        }
    }
    
    /**
     * Returns the number of arguments in this argument list.
     * 
     * @return
     */
    public int length()
    {
        if (delegate == null)
            return sequence.length();
        else
            return length;
    }
    
    /**
     * Returns a "sub"-list that views a portion of this list. This could be compared to
     * <tt>String.substring()</tt>.
     * 
     * @param offset
     *            The offset at which the sublist is supposed to start
     * @return The new sublist
     */
    public ArgumentList subList(int offset)
    {
        return new ArgumentList(this, offset, length() - offset, context);
    }
    
    public ArgumentList subList(int offset, int length)
    {
        return new ArgumentList(this, offset, length, context);
    }
    
    /**
     * Gets (with a call to <tt>get()</tt>) all arguments in this argument list, puts them
     * into a new String array, and returns the array.
     * 
     * @return
     */
    public String[] evalToArray()
    {
        String[] s = new String[length()];
        for (int i = 0; i < s.length; i++)
        {
            s[i] = get(i);
        }
        return s;
    }
}
