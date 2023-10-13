package com.teger.flscript.exception;

public class SyntaxErrorException extends Exception{
	
	public SyntaxErrorException(String message, int line) {
		super("Lines " + line + " : " +message);
	}

}