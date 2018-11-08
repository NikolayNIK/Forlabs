package ru.kollad.forlabs.model;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents cookie jar.
 */
public class Cookies implements Serializable {
	/** It's cookie jar. Do NOT touch it rawly. */
	private List<String> cookies;

	/**
	 * Creates cookie jar from connection.
	 * @param con Http connection.
	 */
	public Cookies(HttpURLConnection con) {
		cookies = new ArrayList<>();
		getFrom(con);
	}

	/**
	 * Gets cookies from connection.
	 * @param con Http connection.
	 */
	public void getFrom(HttpURLConnection con) {
		// trying to get new cookies
		List<String> cList = con.getHeaderFields().get("Set-Cookie");
		if (cList == null) return;

		// add new cookies!
		for (String cookie : cList)
			cookies.add(cookie.substring(0, cookie.indexOf(';')));
	}

	/**
	 * Returns a cookie by the key.
	 * @param key Key.
	 * @return Cookie.
	 */
	public String get(String key) {
		for (String cookie : cookies)
			if (cookie.startsWith(key))
				return cookie.substring(cookie.indexOf('=') + 1);
		return null;
	}

	/**
	 * Put cookies to request's header of connection.
	 * @param con Http connection.
	 */
	public void putTo(HttpURLConnection con) {
		StringBuilder cookieString = new StringBuilder();
		for (String cookie : cookies) {
			cookieString.append(cookie);
			cookieString.append("; ");
		}
		con.addRequestProperty("Cookie", cookieString.toString());
	}

	/**
	 * Throws out all cookies and gets new from connection.
	 * @param con Http connection.
	 */
	public void replaceAllBy(HttpURLConnection con) {
		cookies.clear();
		getFrom(con);
	}

	/**
	 * Replaces existing cookies with new one.
	 * @param con Http connection.
	 */
	public void replaceBy(HttpURLConnection con) {
		// trying to get cookies
		List<String> cList = con.getHeaderFields().get("Set-Cookie");
		if (cList == null) return;

		// for each cookie...
		for (String cookie : cList) {
			// remove existing cookie
			for (int i = cookies.size() - 1; i >= 0; i--)
				if (cookies.get(i).startsWith(cookie.substring(0, cookie.indexOf('=') + 1))) {
					cookies.remove(i);
					break;
				}
			// add new one
			cookies.add(cookie.substring(0, cookie.indexOf(';')));
		}
	}

	/**
	 * Clears all the cookies. Good bye, cookies!
	 */
	public void clear() {
		cookies.clear();
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
