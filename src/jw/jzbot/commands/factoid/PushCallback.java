package jw.jzbot.commands.factoid;

import java.util.ArrayList;
import java.util.List;

import org.opengroove.utils.English;

import jw.jzbot.crosstalk.Callback;
import jw.jzbot.crosstalk.Command;
import jw.jzbot.crosstalk.ErrorType;
import jw.jzbot.crosstalk.Response;
import jw.jzbot.scope.Messenger;
import jw.jzbot.storage.Factoid;
import jw.jzbot.storage.StorageContainer;

public class PushCallback implements Callback
{
    public Messenger source;
    public String senderNick;
    public List<String> factoids = new ArrayList<String>();
    String targetScope;
    public StorageContainer container;
    public String targetNick;
    public int successfulCount = 0;
    public int failedCount = 0;
    
    public PushCallback(Messenger source, String senderNick, String targetScope,
            StorageContainer container, String targetNick)
    {
        super();
        this.source = source;
        this.senderNick = senderNick;
        this.targetScope = targetScope;
        this.container = container;
        this.targetNick = targetNick;
    }
    
    @Override
    public Command nextCommand(Response response)
    {
        if (factoids.size() == 0)
        {
            source.sendMessage(senderNick + ": Factoids have been successfully pushed to "
                + targetNick + ".");
            return null;
        }
        String name = factoids.get(0);
        factoids.remove(0);
        Factoid factoid = container.getFactoid(name);
        if (factoid == null)
        {
            failedCount++;
            source.sendSpaced(senderNick + ": Factoid " + name
                + " does not exist locally at this scope. It will be skipped.");
            return nextCommand(response);
        }
        successfulCount++;
        return new Command("push", "creationTime", "" + factoid.getCreationTime(),
                "creator", factoid.getCreator(), "creatorNick", factoid.getCreatorNick(),
                "creatorUsername", factoid.getCreatorUsername(), "library", ""
                    + factoid.isLibrary(), "name", factoid.getName(), "restricted", ""
                    + factoid.isRestricted(), "value", factoid.getValue());
    }
    
    @Override
    public void failed(boolean local, ErrorType type, String message)
    {
        source.sendSpaced(senderNick
            + ": An error occurred while trying to push factoids to " + targetNick + ": "
            + message);
    }
    
}
