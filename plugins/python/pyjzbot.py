""" jzbot
This plugin provides useful functions for writing JZBot plugins in Python. It
makes the interface provided by JZBot more pythonic. To use this in a Python
plugin, a plugin simply has to depend on this plugin and then import pyjzbot.
"""

import jw.jzbot as _jzbot
import jw.jzbot.help as _jzbot_help


def add_command(name, function):
    """
    Adds a new global command. This command will override any
    factoids with the same name.
    """
    class TheCommand(_jzbot.Command):
        def getName(self):
            return name
        
        def relevant(self, *args):
            return True
        
        def run(self, *args):
            function(*args)
    
    TheCommand.__name__ = name + "_command"
    _jzbot.JZBot.installCommand(TheCommand())


class _HelpNode(object):
    def __init__(self, pages, name):
        self.pages = pages
        self.name = name
    
    def __getattr__(self, name):
        return _HelpNode(self.pages, self.name + " " + name)
    
    def __setattr__(self, name, value):
        self.pages[self.name + " " + name] = value


class HelpSet(_jzbot_help.HelpProvider):
    def __init__(self, install=True):
        self.pages = {}
        if install:
            _jzbot_help.HelpSystem.installProvider(self)
    
    def __getattr__(self, name):
        return _HelpNode(self.pages, name)
    
    def __setattr__(self, name, value):
        self.pages[name] = value


def init(self):
    pass