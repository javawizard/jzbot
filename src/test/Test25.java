package test;

import org.python.core.PyDictionary;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class Test25
{
    public static void main(String[] args)
    {
        PySystemState state1 = new PySystemState();
        PythonInterpreter interpreter1 = new PythonInterpreter(new PyDictionary(), state1);
        PySystemState state2 = new PySystemState();
        PythonInterpreter interpreter2 = new PythonInterpreter(new PyDictionary(), state2);
        System.out.println("first");
        interpreter1.exec("import sys;sys.ps1='anotherps'");
        System.out.println("first");
        interpreter2.exec("import sys");
        System.out.println("first");
        System.out.println(interpreter2.eval("sys.ps1"));
        System.out.println("first");
        System.out.println(interpreter1.eval("sys").getClass().getName());
        System.out.println(state1);
    }
}
