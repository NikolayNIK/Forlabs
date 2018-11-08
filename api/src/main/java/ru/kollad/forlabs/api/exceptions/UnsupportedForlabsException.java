package ru.kollad.forlabs.api.exceptions;

/**
 * Occurs when parsing from Forlabs become unpredictable.
 */
public class UnsupportedForlabsException extends Exception {
	/**
	 * Standard overriding.
	 * @return Message.
	 */
	@Override
	public String getMessage() {
		return "Something was changed... Forlabs gave us the shit!";
	}
}
