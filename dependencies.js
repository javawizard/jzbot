
// jsvn requires JAVA_HOME to be set. To be sure it is, we'll find it in the registry
// and set it.

var shell = WScript.CreateObject("WScript.Shell");
var fileSystem = WScript.CreateObject("Scripting.FileSystemObject");
var file = fileSystem.CreateTextFile("temp.bat", true);

// Get JDK version and check to see if it is installed.
try
{
  var JDKVersion = shell.RegRead("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\CurrentVersion");
  var JavaHome = shell.RegRead("HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\" + JDKVersion + "\\JavaHome");
  file.WriteLine("echo Java Development Kit Found");
  file.WriteLine("set JAVA_HOME=" + JavaHome);
  file.WriteLine("exit /B 0");
}
catch(e)
{
  file.WriteLine("echo Java Development Kit not found.");
  file.WriteLine("echo Get it at http://java.sun.com/javase/downloads/widget/jdk6.jsp");
  file.WriteLine("exit /B 1");
}

file.close();