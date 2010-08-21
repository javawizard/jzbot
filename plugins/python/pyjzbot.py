""" jzbot
This plugin provides useful functions for writing JZBot plugins in Python. It
makes the interface provided by JZBot more pythonic. To use this in a Python
plugin, a plugin simply has to depend on this plugin and then import pyjzbot.
"""

import jw.jzbot as _jzbot


def add_command(name, function):
    """
    Adds a new global command. This command will override any
    factoids with the same name.
    """
    class TheCommand(_jzbot.Command):
        def getName(self):
            return name
        
        def run(self, *args):
            function(*args)
    
    TheCommand.__name__ = name + "_command"
    _jzbot.JZBot.loadCommand(TheCommand())


def init(self):
    pass