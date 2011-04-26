package jw.jzbot.crosstalk;

import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public interface Handler
{
    public Response runCommand(UserMessenger sender, Messenger source, Command command);
}
