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
     * Resolves the specified argument. Regardless of whether this method (or
     * {@link #get(int)}) has been called before, this forces a re-evaluation of the
     * specified argument.
     * 
     * @param index
     *            The index of the item to resolve
     * @param sink
     *            The sink to resolve into
     */
    public void resolve(int index, Sink sink)
    {
        if (delegate != null)
        {
            delegate.resolve(index + offset, sink);
        }
        else
        {
            sequence.get(index).resolve(sink, context);
        }
    }
    
    public String resolveString(int index)
    {
        StringSink sink = new StringSink();
        resolve(index, sink);
        return sink.toString();
    }
    
    /**
     * Gets the unresolved parsed fact entity at the specified index. Most functions don't
     * need to use this; the primary exception is the {{store}} function.
     * 
     * @param index
     *            The index of the fact entity to get
     * @return The fact entity at that index
     */
    public FactEntity getEntity(int index)
    {
        if (delegate != null)
            return delegate.getEntity(index + offset);
        return sequence.get(index);
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
     * Gets the argument at the specified index. If the argument's value has already been
     * retrieved by a call to this method, this method does <b>not</b> cause the argument
     * to be resolved again. A ForkedSink is used to actually resolve the argument in
     * question. If the argument has already been resolved, then the string version saved
     * by the ForkedSink is written to the sink instead.<br/><br/>
     * 
     * If this caching behavior is not needed, then {@link #resolve(int, Sink)} should be
     * used instead, as <tt>resolve</tt> tends to be more efficient than this method since
     * it doesn't store the result for later use.
     * 
     * @param index
     *            The index of the argument. Indexes start at 0.
     * @return The result of running the argument at the specified index
     */
    public void get(int index, Sink sink)
    {
        if (delegate != null)
        {
            delegate.get(index + offset, sink);
        }
        else
        {
            if (resolved[index] == null)
            {
                StringSink cache = new StringSink();
                ForkedSink fork = new ForkedSink(sink, cache);
                sequence.get(index).resolve(fork, context);
                resolved[index] = cache.toString();
            }
            else
            {
                sink.write(resolved[index]);
            }
        }
    }
    
    /**
     * Creates a {@link StringSink}, calls {@link #get(int, Sink)}, and returns the value
     * in the string sink.
     * 
     * @param index
     * @return
     */
    public String getString(int index)
    {
        // TODO: a speed improvement could be made here by checking to see if the string
        // has been cached, and if it has, that string can just be returned as-is.
        StringSink sink = new StringSink();
        get(index, sink);
        return sink.toString();
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
            StringSink sink = new StringSink();
            get(i, sink);
            s[i] = sink.toString();
        }
        return s;
    }
}
