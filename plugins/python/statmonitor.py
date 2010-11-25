
""" jzbot
This plugin provides a command, monitor, that shows the current performance
statistics of all monitord instances currently connected to the Autobus server.
dependencies: pyjzbot
"""

import sys
if "storage/afn-python/src" not in sys.path:
    sys.path.append("storage/afn-python/src")
from libautobus import AutobusConnection
from pyjzbot import makecommand
import re
import math
import os
from java.lang import System

@makecommand
def monitor(server, channel, pm, sender, source, arguments):
    print "bus is " + str(bus)
    print "host is " + str(bus.host)
    print "input_thread is " + str(bus.input_thread)
    print "is_shut_down is " + str(bus.is_shut_down)
    print "AUTOBUS_SERVER is " + str(os.getenv(""))
    interfaces = bus["autobus"].list_interfaces()
    stat_machines = []
    for interface in interfaces:
        if interface["name"].startswith("monitor."):
            name = interface["name"][len("monitor."):]
            if "." not in name:
                stat_machines.append(name)
    source.sendMessage("\x02CPU:\x0f \x0302user\x0f \x0311system\x0f\x02\x0f "
            "\x02Memory:\x0f \x0303resident\x0f \x0309buffers/cached\x0f\x02\x0f "
            "\x02Swap:\x0f \x0306used\x0f\x02\x0f")
    if len(stat_machines) == 0:
        source.sendMessage("No machines currently providing stats.")
    for machine in stat_machines:
        stats = bus.get_remote_object_value("monitor." + machine, "status")
        if stats is None:
            continue
        user, system = stats["cpu"]
        cpu_bar = create_bar([2, 11], [user, system]) + "   "
        memory_bar = ""
        swap_bar = ""
        if "memory" in stats:
            (resident, buffers, cached, free, total, swap, swapfree,
                    swaptotal) = stats["memory"]
            memory_bar = create_bar([3, 9], [(float(resident) / total) * 100,
                    ((float(buffers) + cached) / total) * 100]) + "   "
            swap_bar = create_bar([6], [(float(swap) / swaptotal) * 100])
        source.sendMessage(cpu_bar + memory_bar + swap_bar + machine) 

def create_bar(colors, percents, width=30):
    """
    Returns some IRC color-formatted text representing a progress bar
    with the specified percent values set to the specified colors, both of
    which should be lists. The percent values can be floats and should range
    from 0 to 100.
    """
    cents_per_bar = 100.0 / width
    total = 0
    text = ""
    for color, percent in zip(colors, percents):
        bars = percent / cents_per_bar
        bars = int(bars)
        if total == 0 and bars == 0: #Guarantee we show at least one colored
            # vertical bar to indicate the color, since color is significant
            # in this plugin (blue = processor, green = memory, purple = swap,
            # red = disk, orange/yellow = network)
            bars = 1
        total += bars
        if total > width:
            total, bars = width, bars - (total - width)
        text += "\x03" + str(color).rjust(2, "0") + ("|" * bars)
    if total < width:
        text += "\x0300" + ("|" * (width - total))
    return "[\x02" + text + "\x0f\x02\x0f]"

def init(context):
    global bus
    bus = AutobusConnection("spiro.afn")
    bus.start_connecting()
