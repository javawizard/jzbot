from __future__ import with_statement

""" jzbot
Adds a command, jzbot-javadoc, that invokes ./javadoc in the jzbot folder to
regenerate the JZBot javadocs. This also adds a no-argument function by the
same name. The command pastebins the output of ./javadoc while the function
outputs it directly. This also registers a global configuration folder,
jzbot-javadoc, that can be used to configure the plugin.
"""

from jw.jzbot import Command, JZBot
from jw.jzbot.fact import Function, FactParser
from jw.jzbot.configuration import Configuration
from jw.jzbot.configuration.Configuration import VarType
from jw.jzbot.pastebin.PastebinUtils import pastebinNotice
from jw.jzbot.utils.Utils import threadedCopy
from java.io import File, ByteArrayOutputStream
from java.lang import Runtime, String
from threading import RLock

generate_lock = RLock()
exec_process = getattr(Runtime.getRuntime(), "exec") # Hack to get around
# "exec" being a reserved keyword in Python

def generate_javadocs():
    with generate_lock:
        process = exec_process(["./javadoc"])
        buffer = ByteArrayOutputStream()
        threadedCopy(process.getInputStream(), buffer)
        threadedCopy(process.getErrorStream(), buffer)
        exit_code = process.waitFor()
        return exit_code, String(buffer.toByteArray())


class JavadocCommand(Command):
    def getName(self):
        return "jzbot-javadoc"
    
    def relevant(self, server, channel, pm, sender, source, arguments):
        return True
    
    def run(self, server, channel, pm, sender, source, arguments):
        if Configuration.getBool(None, "jzbot-javadoc/superop"):
            sender.verifySuperop()
        source.sendMessage("Generating javadocs...")
        exit_code, output = generate_javadocs()
        if exit_code == 0:
            source.sendSpaced("Successful! Javadocs have been regenerated "
                              "into storage/javadocs.")
        else:
            source.sendSpaced("Regeneration of javadocs failed with "
                              "exit code " + str(exit_code) + ".")


class JavadocFunction(Function):
    def evaluate(self, sink, arguments, context):
        exit_code, output = generate_javadocs()
        sink.write(exit_code)
    
    def getHelp(self, topic):
        return ("This function runs the javadoc script located in the bot's "
                "main folder, which (re)generates javadocs for JZBot itself "
                "and its plugin API and stores them in storage/javadocs. "
                "This function then outputs the status code of the javadoc "
                "script.")


def init(context):
    Configuration.register(None, "jzbot-javadoc", "This folder contains "
                           "configuration variables related to the "
                           "jzbot-javadoc plugin.", VarType.folder, None)
    Configuration.register(None, "jzbot-javadoc/superop", "True if only "
                           "superops can invoke the command ~jzbot-javadoc, " 
                           "false if anyone can invoke it.", VarType.bool,
                           "1")
    JZBot.installCommand(JavadocCommand())
    FactParser.installFunction("jzbot-javadoc", JavadocFunction())

















