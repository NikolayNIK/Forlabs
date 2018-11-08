package ru.kollad.forlabs.api.exceptions;

/**
 * Occurs when cookies are too old.
 */
public class OldCookiesException extends Exception {
	/**
	 * Standard overriding.
	 * @return Message.
	 */
	@Override
	public String getMessage() {
		return "Cookies are too old.";
	}
}
