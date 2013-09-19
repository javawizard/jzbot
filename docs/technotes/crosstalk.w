NOTICE: The original protocol has been rewritten. Look toward the end of the file to see the changes.

Crosstalk is a system for allowing bots to communicate and exchange information. A crosstalk session is a command-response driven communications channel controlled by the bot that started the crosstalk session. Various commands can be issued over such a channel.

There's no specific command for instructing a bot to begin a crosstalk session with another bot; instead, various components of JZBot (and, by extension, commands those components provide) are used to instruct the bot to perform some particular action via crosstalk. For example, "~factoid push some-bot @server#channel fact1 fact2 fact3" uses crosstalk to copy the factoids fact1, fact2, and fact3 at the current channel to some-bot, creating (or updating) them in some-bot with channel scope at the channel @server#channel.

Internally, crosstalk sessions are started by calling jw.jzbot.crosstalk.Crosstalk.startSession(), passing in the channel at which to start a session, the nickname of the bot to start a session with, the name of the crosstalk handler to connect to, and the callback that should be called once a session has been established.

Crosstalk messages are always of the form "botname: __crosstalk__ <session> <type> <command> <key1>=<value1> <key2>=<value2> <data>". <session> is the identifier of the crosstalk session; this, right now, is created from System.currentTimeMillis() with a few random digits added onto the end. <type> is "c", "r", "ca", or "ra", for a command, a response to a command, an aggregated command (a command where all of the data can't fit on one line; a sequence of ca commands is terminated by a c command which causes the command to actually be applied), and an aggregated response. <command>, which is only present if <type> is "c" (not when the type is "r", since it can be inferred from the last command sent, and not when the type is "ca" or "ra", since it will be inferred when the corresponding "c" or "r" is sent), is the name of the command to run. <key1>=<value1> and so on are entries that make up the command's properties, and <data> is the extra data for the command. Keys, values, and extra data are all transferred URL-encoded.

If there are too many properties to fit on a single line, if a property's value is too large to fit on a single line, or if the command data is too large to fit on a single line, the message is split up into multiple messages. All such messages except for the last one are sent with the type "ca" or "ra" instead of "c" or "r". Values are combined across "ca" and "ra" messages as specified:

	If a key is sent multiple times, the URL-decoded values are concatenated in the order that they were sent.
	
	If data is sent multiple times, the data is concatenated in the order that it was sent.

A crosstalk session is started by sending the crosstalk recipient a __handshake__ command. Its data is a string of 512 "0" characters; the reason behind this will be mentioned in a bit. It contains the following properties:
	
	version: The version of Crosstalk that the sender is using. The receiver will match this against its version, and if the versions don't match, it will respond with an error.
	
	handler: The name of the handler that should handle this crosstalk session. If the receiver does not know of such a handler, it will respond with an error.
	
The response also contains 512 zeros as data and contains a property named ready whose value is "yes" or "no" depending on whether the handshake succeeded or failed, respectively. If it succeeded, the following additional properties are present:
	
	version: The version of Crosstalk that the receiver is using.
	
	length: The overall length of the received message, not including "bot-name: __crosstalk__ ". This, along with the 512 zeros that are sent to start out the crosstalk command and the 512 zeros sent in reply, allow the initiator to detect any message truncating that the server may be performing (IRC truncates messages to a length typically some tens of characters less than the 512 character line limit).

If it failed, the following additional properties are present:

	error: The reason the handshake failed. Currently, this must be one of "version", indicating that a version mismatch occurred, or "other", indicating some other error occurred.
	
	message: A more detailed message about why the handshake failed.

After this, the initiator will send a message, __ml__, which contains one property, length, whose value is the length seen by the initiator. This ensures that lengths are still observed even if the length of a message from the receiver to the initiator is shorter than the length of a message from the initiator to the receiver. The response to downlength contains no data or properties.

The callback passed into the initiator is then informed that the handshake has finished successfully, and asked for the next command to run and the callback to call when this command finishes.

This command is then sent to the receiver. The receiver will have looked up in its list of handlers and found an appropriate handler during the handshake, which it will have instructed to set up a session. This handler is then informed of the incoming command, and asked for a response.

The response is then sent, with one additional property: __status__, which will have either the value "ok" or the value "error". If it has the value "ok", __status__ is removed from the property list, and the properties and command data are handed to the handler, again asking for the next command and handler.

If the value "error" is received, which, right now, will only occur if the receiver's handler throws an exception, then two additional keys are present, and the response terminates the crosstalk conversation:

	error: Right now, this can only have the value "other".
	
	message: A message indicating what happened. This will most likely include a pastebin to the stack trace that caused the error.

If the initiator's callback throws an exception, the __hangup__ command is sent with some extra properties indicating the error (__hangup__ will be discussed in a bit).

When the initiator's callback returns null instead of the next message to send, this signals the end of the crosstalk exchange. The initiator then sends the __hangup__ command, which usually won't contain any keys. If the hangup was in response to the initiator throwing an exception, however, the hangup will contain two keys, error and message, which have the same meaning as they do in a response to a command where __status__ is "error".

The __hangup__ response also contains no properties. It is the last message sent in a crosstalk conversation unless a response with __status__ set to "error" is sent; in that case, said response is the last message. The initiator typically won't wait for the __hangup__ response to be sent to terminate the session.

Both the initiator's callback and the receiver's handler will be notified when a hangup occurs, and they will be informed of the reason. Similarly, both will be notified when the conversation is terminated early due to __status__ having the value "error".

The initiator and the receiver both have a timer set in case one end dies unexpectedly. I'm thinking this will default to 15 seconds for now. If the initiator waits 15 seconds before hearing from the receiver, it sends a __hangup__ indicating that a timeout occurred, and closes down the session. If the receiver waits 15 seconds before hearing from the initiator, it sends a response containing __status__=error and closes down the session.


==Rewrite==
I'M REWRITING THIS TO BE STATELESS TO SIMPLIY A LOT OF THINGS.

New format: 

	commands: "botname: __crosstalk__ c <messageid> <handler> <command> <key>=<value> ... <data>"
	
	aggregated commands (all but the last message): "botname: __crosstalk__ ca <messageid> <key>=<value> ... <data>"
	
	responses: "botname: __crosstalk__ r <messageid> <status> <key>=<value> ... <data>"
	
	aggregated responses (all but the last message): "botname: __crosstalk__ r <messageid> <key>=<value> ... <data>"






















