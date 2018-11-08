package ru.kollad.forlabs.api.exceptions;

/**
 * Occurs when credentials for Forlabs are incorrect.
 */
public class IncorrectCredentialsException extends Exception {
	/**
	 * Standard overriding.
	 * @return Message.
	 */
	@Override
	public String getMessage() {
		return "Login or password is incorrect!";
	}
}
