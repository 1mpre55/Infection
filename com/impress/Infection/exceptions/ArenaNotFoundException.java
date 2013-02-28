package com.impress.Infection.exceptions;

public class ArenaNotFoundException extends GameException {
	private static final long serialVersionUID = -2176746475985902958L;
	private String arenaName;
	public ArenaNotFoundException(String arenaName) {
		this.arenaName = arenaName;
	}
	public ArenaNotFoundException(String arenaName, String message) {
		super(message);
		this.arenaName = arenaName;
	}
	public String getArenaName() {
		return arenaName;
	}
}