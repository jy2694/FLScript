package com.teger.flscript.exception;

public class InvalidStatementException extends Exception{
	
	public InvalidStatementException(String message, int line) {
		super("Lines " + line + " : " +message);
	}

}
