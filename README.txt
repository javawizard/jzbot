JZBot is an IRC bot. Visit http://jzbot.googlecode.com if
you have questions.

To set up JZBot, make sure you have java. If you don't, 
install it by running "sudo apt-get install sun-java6-jdk".
I haven't tested JZBot with OpenJDK, so if you install that
instead of sun-java6-jdk, your mileage will vary. Then, 
run this command:

chmod +x jzbot

Then run this:

./jzbot

That command will print out info on how to set up the bot.

========== FOR THE TECHNICALLY INCLINED ==========

If you want to write your own functions (see "~help functions"
for info on what functions are), all you need to do is create
a subclass of org.opengroove.jzbot.fact.Function and add a 
line to storage/custom-functions.props with the key being the
name of the function and the value being the fully-qualified
name of your class that extends Function. If 
custom-functions.props does not exist, create it. TODO: make this work

If you've created a function that does something useful, 
and you wouldn't mind making it open-source, visit 
http://jzbot.google.com, contact javawizard (whose email 
address will be in the right sidebar at jzbot.googlecode.com), 
tell me that you have a custom function, and I'll consider 
adding it into the JZBot code. I'll also consider giving you
commit access to the JZBot repository.