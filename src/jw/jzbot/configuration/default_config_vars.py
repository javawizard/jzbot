
# This file contains the default configuration variables registered by JZBot
# itself. This is written in Python because it's ended up being easier to code
# configuration stuff in Python than it has in Java.

from jw.jzbot.configuration import Configuration
import __builtin__

# We're going to override some built-in functions here, but we don't
# particularly care as we're not using any of them after this
bool = Configuration.VarType.bool
integer = Configuration.VarType.integer
decimal = Configuration.VarType.decimal
text = Configuration.VarType.text
folder = Configuration.VarType.folder

# TODO: check through the SVN version history and pull the descriptions
# for these from jw.jzbot.ConfigVars

# Java order:    scope, name, description, type, defaultValue
# Tuple order:   type, name, default, description

variables = [
    (integer, "delay", 1000, "The delay to use for sending messages on "
     "servers with no set delay. This is in milliseconds."),
    (integer, "lqdelay", 30, "The interval, in seconds, at which to "
     "store logs to disk. This helps reduce performance issues by "
     "buffering logs in memory, but may cause logs for up to this many "
     "seconds to be lost whenever the bot shuts down."),
    (integer, "lqmaxsize", 500, "The maximum size of the in-memory "
     "log queue"),
    (text, "notfound", None, "The name of a factoid to invoke when a "
     "non-existent factoid or command is invoked. This will be resolved "
     "at the scope that the factoid or command was invoked at, so "
     "channel-specific or server-specific factoids could allow for "
     "channel-specific or server-specific error messages. If this is "
     "unset, or if the specified factoid cannot be found at whatever "
     "scope the missing command is invoked at, a default message "
     "(currently \"Huh? (pm \"help\" for more info)\") will be used instead."),
    (text, "primary", None, "The bot's primary channel. Various pieces "
     "of information, such as some error messages, will be sent to this "
     "channel. Such information will be discarded if this is unset."),
    (bool, "proxytrace", False, "True to trace all ProxyStorage calls, false"
     "to not do any tracing."),
    (bool, "thegame", True, "True to respond to all private messages "
     "containing any phrases in the config var \"gametext\" with an "
     "additional line saying \"/me just lost "
     "the game\". This line will be sent before the response to the private "
     "message. This does nothing at channels; see \"~regex add\" for a way to "
     "do it at channels. This is intended mostly as a hack until I can "
     "properly implement global regexes."),
    (text, "gametext", "the game", "A list of phrases that the bot will lose "
     "the game to, each separated by a | character. See the config variable "
     "\"thegame\" for more information on what this does.")
]

for var_type, name, default, description in variables:
    if default.__class__ == __builtin__.bool:
        # Convert booleans to their numerical representation since the
        # configuration system stores booleans as "1" and "0" for True and
        # False, respectively.
        default = int(default)
    Configuration.register("", name, description, var_type, 
                           None if default is None else str(default))






















