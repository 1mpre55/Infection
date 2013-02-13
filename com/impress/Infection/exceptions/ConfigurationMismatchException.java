package com.impress.Infection.exceptions;

/**
 * Thrown to indicate that configuration contains invalid data
 * @author 1mpre55
 */
public class ConfigurationMismatchException extends GameException {
	private static final long serialVersionUID = 4907208776524197133L;
	public ConfigurationMismatchException() {
		super();
	}
	public ConfigurationMismatchException(String message) {
		super(message);
	}
}