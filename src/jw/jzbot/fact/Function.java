package jw.jzbot.fact;

import jw.jzbot.help.Help;
import jw.jzbot.help.HelpPage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Function
{
    // public abstract String getName();
    /**
     * Runs this function. The Fact interpreter calls this when a function is to be run to
     * actually run the function. The arguments to the function can be accessed via
     * <tt>arguments</tt>and the context in which the factoid is running (which includes
     * stuff like the factoid's local variables, factoid name, etc) is available via
     * <tt>context</tt>. The function should write its output to <tt>sink</tt> via one or
     * more of the sink's <tt>write</tt> methods.
     *
     * @param sink
     *            The sink to which this function's output should be written
     * @param arguments
     *            The arguments that are being given to this function
     * @param context
     *            The context that this function is being run in, which can be used to get
     *            and modify local, global, and persistent variables, get the channel that
     *            this factoid is running in, get the person that sent the factoid, and so
     *            on.
     */
    public abstract void evaluate(Sink sink, ArgumentList arguments, FactContext context);

    /**
     * Gets help on the specified topic for this function. Most functions should, at the
     * very least, return a value from this describing the function. When a user sends
     * "~help functions &lt;name>", where &lt;name> is this function's name, then this
     * method is called with a topic of &lt;tt>null&lt;/tt>. When a user sends
     * "~help functions &lt;name> &lt;topic>", this method is called with a topic of
     * &lt;topic>.
     *
     * @param topic
     * @return
     */
    public String getHelp(String topic) {
        throw new UnsupportedOperationException("Functions must override one of the getHelp() methods");
    }

    public HelpPage getHelp() {
        return new HelpPage() {
            public String getContent() {
                return getHelp(null);
            }

            public Set<String> getChildNames() {
                Set<String> s = new HashSet<>();
                s.addAll(Arrays.asList(getTopics()));
                return s;
            }

            public HelpPage getChild(String name) {
                if (getChildNames().contains(name)) {
                    return Help.build(getHelp(name));
                } else {
                    return null;
                }
            }
        };
    }

    /**
     * Returns an empty string array. Subclasses should override this and return topics
     * that <tt>getHelp()</tt> responds to if it responds to specific topics other than
     * just null.
     *
     * @return
     */
    public String[] getTopics()
    {
        return new String[0];
    }

    /**
     * Returns the name of this function's category. Categories are used to categorize
     * functions when ~help functionsbycategory is used. They have no other semantic
     * purpose.
     *
     * The default implementation looks at the package this function is in and uses
     * everything after jw.jzbot.fact.functions as the category name. If there's no
     * intermediate package, or if this class isn't in jw.jzbot.fact.functions,
     * "uncategorized" will be returned.
     */
    public String getCategory() {
        Package p = getClass().getPackage();
        if (p == null) {
            return "uncategorized";
        }
        if (!p.getName().startsWith("jw.jzbot.fact.functions.")) {
            return "uncategorized";
        }
        return p.getName().substring("jw.jzbot.fact.functions.".length());
    }
}
