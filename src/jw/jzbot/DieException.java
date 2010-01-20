package jw.jzbot;

import jw.jzbot.fact.exceptions.FactoidException;

/**
 * A subclass of ResponseException that causes the command to be aborted instead of
 * sending a response.
 * 
 * @author Alexander Boyd
 * 
 */
public class DieException extends ResponseException
{
}
