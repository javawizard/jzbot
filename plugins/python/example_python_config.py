
""" jzbot
This is an example plugin written in Python that demonstrates how to use
configuration variables. It also shows how to do things without using pyjzbot.
It provides a single command, ~pyconfigexample.
"""

import jw.jzbot as jzbot
from jw.jzbot.configuration import Configuration

class PyConfigExampleCommand(jzbot.Command):
    def getName(self):
        return "pyconfigexample"
    
    def relevant(self, server, channel, pm, sender, source, arguments):
        return True
    
    def run(self, server, channel, pm, sender, source, arguments):
        source.sendSpaced("The current message is " + 
                          str(Configuration.getText(None, "pyconfigmessage"))
                          + ". To change it, edit the global config "
                          "var pyconfigmessage.")


def init(context):
    jzbot.JZBot.installCommand(PyConfigExampleCommand())
    Configuration.register(None, "pyconfigmessage", 
                           "A message that can be shown with ~pyconfigexample.", 
                           Configuration.VarType.text, None)

