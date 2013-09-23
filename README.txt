        s     S  s
      s s  s  s S
   _____S___S__S______
  (==================_)__________
   \   ...     ...  ´            `
  /|  : O :   : O : | Welcome to |
 | |  :___:   :___: |    JZBot   |
  \_\    _         / ___________-´
     \    -----´  --´
      `-----------´

JZBot is an IRC bot. Visit http://github.com/javawizard/jzbot if
you have questions.

To set up JZBot, make sure you have java. If you don't, 
install it by running "sudo apt-get install sun-java6-jdk".
I haven't tested JZBot with OpenJDK, so if you install that
instead of sun-java6-jdk, your mileage may vary. I also 
haven't tested JZBot on Windows, but it should work without
a problem. Then, run this command:

chmod +x jzbot

Then run this:

./jzbot

That command will print out info on how to set up the bot.

========== FOR THE TECHNICALLY INCLINED ==========

If you want to write your own functions (see "~help functions"
for info on what functions are), all you need to do is create
a subclass of jw.jzbot.fact.Function in the package 
jw.jzbot.fact.functions, and place its .class files in 
classes/jw/jzbot/fact/functions. I know this fixed-package
naming is less than intuitive, and I plan to add a configuration
file in the future that can be used to register functions in 
other classes or folders. I'm also thinking of making a plugins
folder where groups of functions can be registered at a time
by including jar files.

If you've created a function that does something useful, 
and you wouldn't mind making it open-source, visit 
http://github.com/javawizard/jzbot and submit a pull request.
