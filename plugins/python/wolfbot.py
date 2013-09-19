
""" jzbot
This plugin allows the bot to arbitrate a game of Werewolf, also known as
Mafia or Murder in the Dark. It can moderate games played solely via pm and
games played at channels. Use "help wolfbot" for more info.
dependencies: pyjzbot
"""

import pyjzbot

help = pyjzbot.HelpSet()
help.wolfbot = """The wolfbot plugin allows the bot to arbitrate a game of
werewolf, also known as Mafia or Murder in the Dark."""

#   kill shoot 