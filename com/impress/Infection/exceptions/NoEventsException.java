package com.impress.Infection.exceptions;

public class NoEventsException extends GameException {
	private static final long serialVersionUID = -3532351138796062384L;
	public NoEventsException() {}
	public NoEventsException(String message) {
		super(message);
	}
}