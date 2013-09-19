package jw.jzbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jw.jzbot.fact.functions.HashFunction;


public class Hashbuilder
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        System.out.println("enter some text to hash.");
        String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println(HashFunction.doHash(s));
    }
    
}
