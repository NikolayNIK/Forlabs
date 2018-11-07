package ru.kollad.forlabs.api.exceptions;

public class IncorrectCredentialsException extends Exception {
	@Override
	public String getMessage() {
		return "Login or password is incorrect!";
	}
}
