
import jw.jzbot

""" jzbot
This is an example plugin written in Python. It installs a command,
pythonexample, that prints out a test message.
"""

class ExampleCommand(jw.jzbot.Command):
    def getName(self):
        return "pythonexample"

    def run(self, server, channel, pm, sender, source, arguments):
        source.sendMessage("This is an example response from a Python plugin")

def init(context):
    jw.jzbot.JZBot.loadCommand(ExampleCommand())

