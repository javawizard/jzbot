from __future__ import with_statement

""" jzbot
This plugin provides a quote management system and an optional web interface
to view quotes. It does not provide any commands; it only provides functions,
and it's up to superops to write commands to use those functions. It provides
{addquote}, {deletequote}, {getquote}, and {searchquotes}. It also registers
a global configuration folder named quote. It allows for quotes to be added to
various quote groups, which can be used to separate sets of quotes.
"""

from jw.jzbot.configuration import Configuration
from jw.jzbot.configuration.Configuration import VarType
from jw.jzbot.events import Notify, ScopeListener
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from com.ziclix.python.sql import zxJDBC
from threading import RLock


def init(context):
    global db
    db = zxJDBC.connect("jdbc:h2:" + context.storageFolder.getPath() + 
                     "/quotedb/db", "sa", "", "org.h2.Driver")
    
    



