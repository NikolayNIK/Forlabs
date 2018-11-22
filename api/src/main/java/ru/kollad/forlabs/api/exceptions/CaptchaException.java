package ru.kollad.forlabs.api.exceptions;

public class CaptchaException extends Exception {
	@Override
	public String getMessage() {
		return "Captcha was occurred.";
	}
}
