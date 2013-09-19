package jw.jzbot.fact.functions.net;

import java.text.SimpleDateFormat;
import java.util.Date;

import jw.boardforj.NormalDate;
import jw.boardforj.Post;
import jw.boardforj.Response;
import jw.boardforj.TheBoard;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.output.DelimitedSink;

public class TheboardFunction extends Function
{
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy.MM");
    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
    public static final TheBoard board = new TheBoard();
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            String command = arguments.resolveString(0);
            arguments = arguments.subList(1);
            if (command.equals("today"))
            {
                sink.write(dayFormat.format(board.getToday()));
            }
            else if (command.equals("years"))
            {
                writeDateList(board.getYears(), yearFormat, sink);
            }
            else if (command.equals("months"))
            {
                NormalDate date =
                        new NormalDate(yearFormat.parse(arguments.resolveString(0)));
                writeDateList(board.getMonths(date), monthFormat, sink);
            }
            else if (command.equals("days"))
            {
                NormalDate date =
                        new NormalDate(monthFormat.parse(arguments.resolveString(0)));
                writeDateList(board.getDays(date), dayFormat, sink);
            }
            else if (command.equals("posts"))
            {
                NormalDate date =
                        new NormalDate(dayFormat.parse(arguments.resolveString(0)));
                DelimitedSink s = new DelimitedSink(sink, " ");
                for (int id : board.getPostIds(date))
                {
                    s.next();
                    s.write(id);
                }
            }
            else if (command.equals("text"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                sink.write(post.text);
            }
            else if (command.equals("date"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                sink.write(dayFormat.format(post.date));
            }
            else if (command.equals("categories"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                DelimitedSink s = new DelimitedSink(sink, "\n");
                for (String category : post.categories)
                {
                    s.next();
                    s.write(category);
                }
            }
            else if (command.equals("responsecount"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                sink.write(post.responses.length);
            }
            else if (command.equals("responsetext"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                Response response =
                        post.responses[Integer.parseInt(arguments.resolveString(1))];
                sink.write(response.text);
            }
            else if (command.equals("responsealias"))
            {
                Post post = getPost(context, Integer.parseInt(arguments.resolveString(0)));
                Response response =
                        post.responses[Integer.parseInt(arguments.resolveString(1))];
                sink.write(response.alias);
            }
            else
            {
                throw new FactoidException("No such command: " + command);
            }
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    public static void writeDateList(NormalDate[] dates, SimpleDateFormat format, Sink sink)
    {
        DelimitedSink s = new DelimitedSink(sink, " ");
        for (Date date : dates)
        {
            s.next();
            s.write(format.format(date));
        }
    }
    
    /**
     * Gets the post with the specified id. If the post is present in the context's object
     * storage, then the post will be returned from there. Otherwise, the post will be
     * downloaded from the 100-hour board server, put into the context's object storage,
     * and returned.
     * 
     * @param context
     *            The context to use
     * @param id
     *            The id of the post to get
     * @return The post object
     */
    public Post getPost(FactContext context, int id)
    {
        Post post = (Post) context.objectStorage.get("theboard-" + id);
        if (post != null)
        {
            System.out.println("THEBOARD: Post " + id
                + " obtained from the factoid-local object store.");
            return post;
        }
        System.out.println("THEBOARD: Downloading post " + id + "...");
        // Let's time it, just for the random heck of it
        long time = System.currentTimeMillis();
        post = board.getPost(id);
        System.out.println("THEBOARD: Downloaded post " + id + " in "
            + (System.currentTimeMillis() - time) + " ms.");
        context.objectStorage.put("theboard-" + id, post);
        return post;
    }
    
    @Override
    public String getHelp(String topic)
    {
        if (topic == null)
            return "Syntax: {theboard|<command>|...} -- Accesses the BYU 100-hour "
                + "board. Use \"%HELPCMD functions theboard <command>\" for help "
                + "with a particular subcommand. Dates are formatted as yyyy, "
                + "yyyy.MM, or yyyy.MM.dd, depending on whether the date needs "
                + "to have a year, a month, or a day. It's generally obvious from "
                + "the commands which are which. Unless otherwise noted, lists are "
                + "returned as space-separated strings. A particular post will only "
                + "be retrieved once from the board for any given factoid invocation, "
                + "even if multiple functions are called on it. Available commands are: "
                + "today years months days posts text date categories responsecount "
                + "responsetext responsealias";
        if (topic.equals("today"))
            return "Syntax: {theboard|today} -- Returns the latest date for which "
                + "the board has posts.";
        if (topic.equals("years"))
            return "Syntax: {theboard|years} -- Returns a list of years for which "
                + "the board has posts.";
        if (topic.equals("months"))
            return "Syntax: {theboard|months|<year>} -- Returns a list of months for "
                + "which the board has posts in the specified year.";
        if (topic.equals("days"))
            return "Syntax: {theboard|days|<month>} -- Returns a list of days for "
                + "which the board has posts in the specified month.";
        if (topic.equals("posts"))
            return "Syntax: {theboard|posts|<day>} -- Returns a list of post ids "
                + "that the board has for the specified day.";
        if (topic.equals("text"))
            return "Syntax: {theboard|text|<id>} -- Returns the text of the question "
                + "or comment of the specified post.";
        if (topic.equals("date"))
            return "Syntax: {theboard|date|<id>} -- Returns the date for the specified "
                + "question. I'm not exactly sure at present if this is the date the "
                + "question was asked on or if this was the date the question first "
                + "appeared on the board.";
        if (topic.equals("categories"))
            return "Syntax: {theboard|categories|<id>} -- Returns a newline-separated "
                + "list of categories that this post has been assigned to. I'm not "
                + "100% sure, but I think the way to differentiate between a question "
                + "and a comment is that comments have the category \"Comment\" "
                + "assigned to them.";
        if (topic.equals("responsecount"))
            return "Syntax: {theboard|responsecount|<id>} -- Returns the number of "
                + "responses available for the specified post.";
        if (topic.equals("responsetext"))
            return "Syntax: {theboard|responsetext|<id>|<index>} -- Returns the text "
                + "of the response at the specified index for the post of the "
                + "specified id. Indexes are 0-based.";
        if (topic.equals("responsealias"))
            return "Syntax: {theboard|responsealias|<id>|<index>} -- Returns the "
                + "alias of the person that posted the response at the specified "
                + "index for the post of the specified id. Indexes are 0-based. "
                + "Typically, the alias will be included near the end of the response "
                + "text itself, so you probably won't need this function unless you're "
                + "trying to categorize responses or questions by response writer.";
        return "No such command.";
    }
    
}
