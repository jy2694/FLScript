package com.teger.flscript.exception;

public class VariableNotDefined extends Exception{
	
	public VariableNotDefined(String message, int line) {
		super("Lines " + line + " : " +message);
	}

}
