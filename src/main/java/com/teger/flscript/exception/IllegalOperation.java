package com.teger.flscript.exception;

public class IllegalOperation extends Exception{
	
	public IllegalOperation(String message, int line) {
		super("Lines " + line + " : " +message);
	}

}
