package com.impress.Infection.exceptions;

/**
 * Thrown when a player is trying to join a game that's already full
 * @author 1mpre55
 */
public class GameFullException extends GameException {
	private static final long serialVersionUID = -6419011305834553537L;
	private int maxPlayers;
	public GameFullException(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
}