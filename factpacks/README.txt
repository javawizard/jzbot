Factpacks are packages of factoids that might interest you. You import a factpack into
the bot by sending each line as a message on a channel that the bot is connected to, 
where the bot is using the trigger "~". They are, by default, imported as channel-specific
factoids; this can be overriden by doing a search-and-replace of the factpack file, searching
for "~factoid create" and replacing with "~factoid global create". 

In the future, there will be a mechanism to import factpacks via a pastebin post. This
will essentially interpret each line as a message, with the restriction that the lines can
only start with "~factoid create" or "~factoid global create", and a single error will cause
the rest of the lines to be aborted and all changes made to be undone. "~restrict" will also
be a valid line start command.

Factpacks have the extension ".jfact", indicating that they are JZBot-style factpacks.