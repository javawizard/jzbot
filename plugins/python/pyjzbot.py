""" jzbot
This plugin provides useful functions for writing JZBot plugins in Python. It
makes the interface provided by JZBot more pythonic. To use this in a Python
plugin, a plugin simply has to depend on this plugin and then import pyjzbot.
"""

import jw.jzbot as _jzbot
import jw.jzbot.help as _jzbot_help
from jw.jzbot.fact import FactParser, Function
import re


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


def makecommand(function):
    """
    A version of add_command that can decorate a function, and will cause that
    function to be registered as a command as its name.
    """
    add_command(function.__name__, function)
    return function

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


class _NoExceptions:
    def __enter__(self):
        pass
    def __exit__(self, type, value, traceback):
        return True

no_exceptions = _NoExceptions()


class makefunction(Function):
    def __init__(self, function):
        self.target = function
        self.helptext = function.__doc__
        FactParser.installFunction(function.__name__, self)
    
    def evaluate(self, *args):
        self.target(*args)
    
    def getHelp(self, topic):
        # Remove all multi-space runs and newlines (replacing
        # newlines with spaces)
        return re.sub(" +", " ", self.helptext.replace("\n", " "))


def init(context):
    pass
