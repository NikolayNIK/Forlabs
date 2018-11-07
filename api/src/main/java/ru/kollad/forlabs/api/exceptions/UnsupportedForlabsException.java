package ru.kollad.forlabs.api.exceptions;

public class UnsupportedForlabsException extends Exception {
	@Override
	public String getMessage() {
		return "Something was changed... Forlabs gave us the shit!";
	}
}
