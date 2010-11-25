
""" jzbot
This plugin provides a command, monitor, that shows the current performance
statistics of all monitord instances currently connected to the Autobus server.
dependencies: pyjzbot
"""

from libautobus import AutobusConnection
from pyjzbot import makecommand
import re

@makecommand
def monitor(server, channel, pm, sender, source, arguments):
    interfaces = bus["autobus"].list_interfaces()
    stat_machines = []
    for interface in interfaces:
        if interface["name"].startswith("monitor."):
            name = interface["name"][len("monitor."):]
            if "." not in name:
                stat_machines.append()
    source.sendMessage("\x02CPU:\x15 \x0302user\x15 \x0311system\x15\x02\x15")

def init(context):
    global bus
    bus = AutobusConnection()
    
    bus.start_connecting()
