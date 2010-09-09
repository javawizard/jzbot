from __future__ import with_statement

""" jzbot
This plugin provides a quote management system and an optional web interface
to view quotes. It does not provide any commands; it only provides functions,
and it's up to superops to write commands to use those functions. It provides
{addquote}, {deletequote}, {getquote}, {getquoteinfo}, and {searchquotes}. 
It also registers a global configuration folder named quote. It allows for 
quotes to be added to various quote groups, which can be used to separate sets
of quotes.
dependencies: pyjzbot
"""

from jw.jzbot.configuration import Configuration
from jw.jzbot.configuration.Configuration import VarType
from jw.jzbot.events import Notify, ScopeListener
from jw.jzbot.fact.exceptions import FactoidException
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from com.ziclix.python.sql import zxJDBC
from threading import RLock
from pyjzbot import makefunction, no_exceptions
from java.lang.System import currentTimeMillis

quote_lock = RLock()

def execute(*args):
    cursor = db.cursor()
    cursor.execute(*args)
    return cursor

def execute_commit(*args):
    execute(*args)
    db.commit()

def add_quote_info(quotegroup, quotenumber, **map):
    for key in map:
        execute_commit("insert into quoteinfo values (?,?,?,?)", [quotegroup,
                quotenumber, key, map[key]])

@makefunction
def addquote(sink, arguments, context):
    """
    Syntax: {addquote|<group>|<quote>} -- Adds a new quote to the specified
    group. This function then evaluates to the number that the new quote
    was assigned. Numbering starts at 1 for blank groups. Groups must already
    be present in the quote system configuration before quotes can be added
    to them; see ~config global quote.
    """
    with quote_lock:
        quotegroup = arguments.resolveString(0)
        quotetext = arguments.resolveString(1)
        if not quotegroup in Configuration.getText(None, 
                             "quote/groupnames").split(" "):
            raise FactoidException("The group " + quotegroup
                                 + " does not exist.")
        next_id_result = execute("select nextquote from quotesequence where "
                                 "quotegroup = ?", [quotegroup]).fetchone()
        if next_id_result is None:
            next_id_result = 0
            execute_commit("insert into quotesequence values (?, 0)", [quotegroup])
        else:
            next_id_result = next_id_result[0]
        next_id_result += 1
        execute_commit("update quotesequence set nextquote = ? where quotegroup = ?",
                [next_id_result, quotegroup])
        execute_commit("insert into quotes values(?, ?, ?, ?)", [quotegroup,
                next_id_result, quotetext, False])
        add_quote_info(quotegroup, next_id_result, 
                       nick=context.getSender().getNick(), 
                       user=context.getSender().getUsername(), 
                       host=context.getSender().getHostname(), 
                       server=context.getSender().getServerName(), 
                       scope=context.getCanonicalName(),
                       date=str(currentTimeMillis()))
        sink.write(next_id_result)

@makefunction
def deletequote(sink, arguments, context):
    """
    Syntax: {deletequote|<group>|<number>} -- Deletes the quote with the
    specified number from the specified group. Note that the quote still
    persists on disk in case someone needs to recover it; if sensitive
    information was accidentally quoted, one of the bot's owners should be
    contacted and asked to remove the information from disk.
    """
    quotegroup = arguments.resolveString(0)
    quotenumber = int(arguments.resolveString(1))
    with quote_lock:
        execute_commit("update quotes set hidden = true where quotegroup = ? "
                "and quotenumber = ?", [quotegroup, quotenumber])

@makefunction
def getquote(sink, arguments, context):
    """
    Syntax: {getquote|<group>|<number>} -- Evaluates to the quote with the
    specified number in the specified group. If there is no such quote, this
    function evaluates to the empty string.
    """
    quotegroup = arguments.resolveString(0)
    quotenumber = int(arguments.resolveString(1))
    with quote_lock:
        result = execute("select quotetext from quotes where hidden = false "
                         "and quotegroup = ? and quotenumber = ?",
                         [quotegroup, quotenumber]).fetchone()
        if result is None: # No such quote
            return
        sink.write(result[0])

@makefunction
def getquoteinfo(sink, arguments, context):
    """
    Syntax: {getquoteinfo|<group>|<number>|<key>} -- Gets information about the
    specified quote. <key> specifies the information to retrieve. Valid keys
    are, at present, nicename, nick, user, host, server, date, and scope. All 
    of them pertain to the user that created the quote except for scope, which
    is the scope the user was using at the time they invoked the command.
    """
    quotegroup = arguments.resolveString(0)
    quotenumber = int(arguments.resolveString(1))
    key = arguments.resolveString(2)
    with quote_lock:
        result = execute("select value from quoteinfo where quotegroup = ? and "
                         "quotenumber = ? and key = ?", 
                         [quotegroup, quotenumber, key]).fetchone()
        if result is None:
            return 
        sink.write(result[0])

@makefunction
def searchquotes(sink, arguments, context):
    """
    Syntax: {searchquotes|<group>|<regex>} -- Evaluates to a space-separated
    list of the numbers of all of the quotes in the specified group that match
    the specified regular expression.
    """
    quotegroup = arguments.resolveString(0)
    regex = arguments.resolveString(1)
    with quote_lock:
        result = execute("select quotenumber from quotes where quotegroup = ?"
                         " and quotetext regexp ? and hidden = false order "  
                         "by quotenumber desc", 
                         [quotegroup, regex]).fetchall()
        sink.write(" ".join([str(i[0]) for i in result]))


def init(context):
    global db
    db = zxJDBC.connect("jdbc:h2:" + context.storageFolder.getPath() + 
                     "/quotedb/db", "sa", "", "org.h2.Driver")
    execute("create table if not exists quotes (quotegroup text, "
            "quotenumber int, quotetext text, hidden boolean)")
    execute("create table if not exists quoteinfo (quotegroup text, "
            "quotenumber int,key text, value text)")
    execute("create table if not exists quotesequence (quotegroup text, "
            "nextquote int)")
    Configuration.register(None, "quote", "This folder holds configuration "
                           "variables related to the quote plugin.", 
                           VarType.folder, None)
    Configuration.register(None, "quote/groupnames", "A space-separated list "
                           "of groups that quotes can be added to.",
                           VarType.text, "")
    
    
    



