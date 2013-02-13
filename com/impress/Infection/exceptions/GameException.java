package com.impress.Infection.exceptions;

/**
 * General exception
 * @author 1mpre55
 */
public class GameException extends Exception {
	private static final long serialVersionUID = -6998470977606796667L;
	public GameException() {
		super();
	}
	public GameException(String message) {
		super(message);
	}
}