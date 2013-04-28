package com.impress.Infection.exceptions;

public class CircularInheritanceException extends GameException {
	private static final long serialVersionUID = 2065362014643640057L;
	public CircularInheritanceException() {
		super();
	}
	public CircularInheritanceException(String message) {
		super(message);
	}
}