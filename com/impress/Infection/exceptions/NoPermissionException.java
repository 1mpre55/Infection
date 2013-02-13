package com.impress.Infection.exceptions;

/**
 * Thrown to indicate that a player or another {@link org.bukkit.permissions.Permissible} entity lacks certain permissions
 * @author 1mpre55
 */
public class NoPermissionException extends GameException {
	private static final long serialVersionUID = 3996774524968367247L;
	private String permission;
	public NoPermissionException(String permission) {
		this.permission = permission;
	}
	public NoPermissionException(String permission, String message) {
		super(message);
		this.permission = permission;
	}
	public String getPermission() {
		return permission;
	}
}