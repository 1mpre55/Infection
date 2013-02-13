package com.impress.Infection.exceptions;

/**
 * Thrown to indicate that the game does not contain requested team.
 * @author 1mpre55
 */
public class TeamNotFoundException extends GameException {
	private static final long serialVersionUID = -1882810818985952741L;
	public TeamNotFoundException() {
		super();
	}
	public TeamNotFoundException(String message) {
		super(message);
	}
}