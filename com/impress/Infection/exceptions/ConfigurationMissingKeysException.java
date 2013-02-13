package com.impress.Infection.exceptions;

/**
 * Thrown to indicate that configuration is missing some of the required keys.
 * @author 1mpre55
 */
public class ConfigurationMissingKeysException extends GameException {
	private static final long serialVersionUID = 8135935959705425004L;
	public ConfigurationMissingKeysException() {
		super();
	}
	public ConfigurationMissingKeysException(String message) {
		super(message);
	}
}