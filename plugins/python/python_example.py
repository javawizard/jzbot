
import pyjzbot

""" jzbot
dependencies: pyjzbot
This is an example plugin written in Python. It installs a command,
pythonexample, that prints out a test message.
"""


def init(context):
    pyjzbot.add_command("pythonexample", 
        lambda server, channel, pm, sender, source, arguments:
        source.sendMessage("This is an example response from a Python plugin"))

