package com.impress.Infection.exceptions;

/**
 * Thrown when attempting to make a player join a new game while the player is already playing. 
 * @author 1mpre55
 */
public class AlreadyPlayingException extends GameException {
	private static final long serialVersionUID = 5449305073446763103L;
	public AlreadyPlayingException() {
		super();
	}
	public AlreadyPlayingException(String message) {
		super(message);
	}
}