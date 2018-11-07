package ru.kollad.forlabs.model;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cookies {
	private List<String> cookies;

	public Cookies(HttpURLConnection con) {
		cookies = new ArrayList<>();
		getFrom(con);
	}

	public void getFrom(HttpURLConnection con) {
		List<String> cList = con.getHeaderFields().get("Set-Cookie");
		if (cList == null) return;

		for (String cookie : cList)
			cookies.add(cookie.substring(0, cookie.indexOf(';')));
	}

	public String get(String key) {
		for (String cookie : cookies)
			if (cookie.startsWith(key))
				return cookie.substring(cookie.indexOf('=') + 1);
		return null;
	}

	public void putTo(HttpURLConnection con) {
		StringBuilder cookieString = new StringBuilder();
		for (String cookie : cookies) {
			cookieString.append(cookie);
			cookieString.append("; ");
		}
		con.addRequestProperty("Cookie", cookieString.toString());
	}

	public void replaceAllBy(HttpURLConnection con) {
		cookies.clear();
		getFrom(con);
	}

	public void replaceBy(HttpURLConnection con) {
		List<String> cList = con.getHeaderFields().get("Set-Cookie");
		if (cList == null) return;

		for (String cookie : cList) {
			for (int i = cookies.size() - 1; i >= 0; i--)
				if (cookies.get(i).startsWith(cookie.substring(0, cookie.indexOf('=') + 1))) {
					cookies.remove(i);
					break;
				}
			cookies.add(cookie.substring(0, cookie.indexOf(';')));
		}
	}

	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%d in cookie jar.", cookies.size());
	}

	@Override
	public int hashCode() {
		return cookies.hashCode();
	}
}
