package jw.jzbot.crosstalk.handlers.factoid;

import jw.jzbot.commands.factoid.FactoidCommand;
import jw.jzbot.crosstalk.Command;
import jw.jzbot.crosstalk.Handler;
import jw.jzbot.crosstalk.Response;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public class FactoidHandler implements Handler
{
    
    @Override
    public Response runCommand(UserMessenger sender, Messenger source, Command command)
    {
        return FactoidCommand.processCrosstalkCommand(command);
    }
    
}
