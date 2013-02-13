package com.impress.Infection.exceptions;

/**
 * Thrown when a player is trying to join a game that has already started when that is not allowed.
 * @author 1mpre55
 */
public class GameAlreadyStartedException extends GameException {
	private static final long serialVersionUID = 5449305073446763103L;
	public GameAlreadyStartedException() {
		super();
	}
	public GameAlreadyStartedException(String message) {
		super(message);
	}
}