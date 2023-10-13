package com.teger.flscript.exception;

public class IllegalFunctionArgumentException extends Exception{
	
	public IllegalFunctionArgumentException(String message, int line) {
		super("Lines " + line + " : " +message);
	}

}
