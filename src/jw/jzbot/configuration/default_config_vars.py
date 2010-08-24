
from jw.jzbot.configuration import Configuration

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
    (bool, "proxytrace", "0", "True to trace all ProxyStorage calls, false"
     "to not do any tracing."),
]

for var_type, name, default, description in variables:
    Configuration.register("", name, description, var_type, default)