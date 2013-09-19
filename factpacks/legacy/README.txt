Factpacks are packages of factoids that might interest you. You can install factpacks in one 
of two ways:

  For factpacks that are located in this folder (the factpacks folder), you can install them 
  by sending "~factoid pack install <packname>" to your bot. <packname> is the canonical 
  name of the factpack, which can be found by opening the factpack file and searching for
  "name=". The text that comes after "name=" is the canonical name of the factpack. For 
  example, the canonical name of the factpack that resides in the file google.jzf is
  "local.misc.google".
  
  For factpacks that aren't located in this folder (for example, factpacks that are available
  on a website or factpacks that you're writing yourself), you can install them by placing 
  the factpack's contents into a pastebin post, and then sending "~factoid pack install 
  http://pastebin.com/<postid>" to your bot. For details on the format of a factpack's 
  contents, see docs/technotes/factpacks.txt under the folder that you installed JZBot to.