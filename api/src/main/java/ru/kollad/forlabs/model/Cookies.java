package ru.kollad.forlabs.model;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents cookie jar.
 */
public class Cookies extends ArrayList<String> implements Serializable {

	private static final long serialVersionUID = 919870098686270412L;

	/**
	 * Creates cookie jar from connection.
	 * @param con Http connection.
	 */
	public Cookies(HttpURLConnection con) {
		getFrom(con);
	}

	/**
	 * Gets cookies from connection.
	 * @param con Http connection.
	 */
	private void getFrom(HttpURLConnection con) {
		// trying to get new cookies
		List<String> cList = con.getHeaderFields().get("Set-Cookie");
		if (cList == null) return;

		// add new cookies!
		for (String cookie : cList)
			add(cookie.substring(0, cookie.indexOf(';')));
	}

	/**
	 * Returns a cookie by the key.
	 * @param key Key.
	 * @return Cookie.
	 */
	public String get(String key) {
		for (String cookie : this)
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
		for (String cookie : this) {
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
		clear();
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
			for (int i = size() - 1; i >= 0; i--)
				if (get(i).startsWith(cookie.substring(0, cookie.indexOf('=') + 1))) {
					remove(i);
					break;
				}
			// add new one
			add(cookie.substring(0, cookie.indexOf(';')));
		}
	}
}
