package ru.kollad.forlabs.api;

import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.kollad.forlabs.api.exceptions.IncorrectCredentialsException;
import ru.kollad.forlabs.api.exceptions.OldCookiesException;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;
import ru.kollad.forlabs.model.*;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Provides some methods for working with Forlabs.
 */
public class API {
	/** Cloudflare hack. */
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0";
	/** URL - Login. */
	private static final String LOGIN_URL = "https://forlabs.ru/login";
	/** Mask - Login. */
	private static final String LOGIN_FORM_MASK = "_token=${token}&email=${email}&password=${password}&remember=on";
	/** URL - Logout */
	private static final String LOGOUT_URL = "https://forlabs.ru/logout";
	/** URL - Studies. */
	private static final String STUDIES_URL = "https://forlabs.ru/studies";
	/** URL - Upload. */
	private static final String UPLOAD_URL = "https://forlabs.ru/upload";
	/** Mask - Upload. */
	private static final String UPLOAD_MASK = "?flowChunkNumber=1&flowChunkSize=1048576&" +
			"flowCurrentChunkSize=${size}&flowTotalSize=${size}&flowIdentifier=${size}-${filename2}&" +
			"flowFilename=${filename}&flowRelativePath=${filename}&flowTotalChunks=1";
	/** URL - Dashboard */
	private static final String DASHBOARD_URL = "https://forlabs.ru/dashboard";

	/** Parser. For parsing HTML pages. */
	private static Parser p = Parser.htmlParser();
	/** Cookie Jar. Take one if you dare. */
	private Cookies cookies;
	/** Object for student info. */
	private StudentInfo studentInfo;
	/** Object for studies. */
	private Studies studies;

	/** Empty constructor. */
	public API() {
	}

	/**
	 * Oh, you came with your cookie jar! Please, come in!
	 * @param cookies Cookie Jar.
	 */
	public API(Cookies cookies) {
		this.cookies = cookies;
	}

	/**
	 * Returns MIME-type for specified URL.
	 * Used in multipart sending.
	 * @param url URL.
	 * @return MIME-type.
	 */
	private static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}

	/**
	 * Logs in.
	 * @param email E-mail.
	 * @param password Password.
	 */
	public StudentInfo login(String email, String password) throws IOException, UnsupportedForlabsException, IncorrectCredentialsException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);

		// download login page
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// read cookies
		cookies = new Cookies(con);

		// parse that thing
		Document doc = p.parseInput(response.toString(), LOGIN_URL);

		// get elements with token
		Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
		if (elements.size() == 0) {
			throw new UnsupportedForlabsException();
		}
		String token = elements.get(0).attr("value");

		// setup connection
		con = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// post login form
		OutputStream os = con.getOutputStream();
		os.write(LOGIN_FORM_MASK.replace("${token}", URLEncoder.encode(token, "utf-8"))
				.replace("${email}", URLEncoder.encode(email, "utf-8"))
				.replace("${password}", URLEncoder.encode(password, "utf-8"))
				.getBytes());
		os.close();

		// if redirection wasn't occurred, throw some exceptions
		if (con.getResponseCode() != 302)
			throw new UnsupportedForlabsException();

		// get redirection
		String redirection = con.getHeaderField("Location");
		if (redirection.contains("login"))
			throw new IncorrectCredentialsException();
		cookies.replaceBy(con);

		// setup connection
		con = (HttpURLConnection) new URL(redirection).openConnection();
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// download dashboard
		sc = new Scanner(con.getInputStream(), "utf-8");
		response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// parse dashboard and fill infos
		studentInfo = new StudentInfo(p.parseInput(response.toString(), redirection));

		// get new cookies
		cookies.replaceBy(con);

		return studentInfo;
	}

	/**
	 * Logs out.
	 */
	public void Logout() throws IOException, UnsupportedForlabsException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(DASHBOARD_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, that means we already logged out
		if (con.getResponseCode() == 302)
			return;

		// download dashboard
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// parse that thing
		Document doc = p.parseInput(response.toString(), DASHBOARD_URL);

		// get elements with token
		Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
		if (elements.size() == 0) {
			throw new UnsupportedForlabsException();
		}
		String token = elements.get(0).attr("value");

		// setup connection
		con = (HttpURLConnection) new URL(LOGOUT_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// post login form
		OutputStream os = con.getOutputStream();
		os.write(("_token=" + token).getBytes());
		os.close();

		// if redirection wasn't occurred, throw some exceptions
		if (con.getResponseCode() != 302)
			throw new UnsupportedForlabsException();

		// clear cookies
		cookies.clear();
	}

	/**
	 * Fetch some info from dashboard.
	 * @return Student info.
	 */
	public StudentInfo fetchDashboard() throws IOException, OldCookiesException, UnsupportedForlabsException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(DASHBOARD_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// download dashboard
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// parse dashboard and fill infos
		studentInfo = new StudentInfo(p.parseInput(response.toString(), DASHBOARD_URL));

		// get new cookies
		cookies.replaceBy(con);

		return studentInfo;
	}

	/**
	 * Returns cookie jar.
	 * @return Cookie Jar.
	 */
	public Cookies getCookies() {
		return cookies;
	}

	/**
	 * Fetch some studies.
	 * @return Studies.
	 */
	public Studies getStudies() throws IOException, UnsupportedForlabsException, OldCookiesException {
		// if we already have some studies, return these
		if (studies != null) return studies;

		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(STUDIES_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// read the page
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// get some fresh cookies
		cookies.replaceBy(con);

		// parse the page
		Elements elements = p.parseInput(response.toString(), STUDIES_URL).getElementsByClass("col-sm-6");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();

		// fill studies
		studies = new Studies(elements);

		return studies;
	}

	/**
	 * Returns student info.
	 * @return Student info.
	 */
	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	/**
	 * Fetch some info about study.
	 * @param study Study.
	 * @return Same study, but with info inside!
	 */
	public Study fetchStudy(Study study) throws IOException, UnsupportedForlabsException, ParseException, JSONException, OldCookiesException {
		study.fetch(p, cookies);
		return study;
	}

	/**
	 * Gets task attachments.
	 * @param task Task.
	 * @return Attachments
	 */
	public List<Attachment> getTaskAttachments(Task task) throws IOException, UnsupportedForlabsException, JSONException, OldCookiesException {
		return task.fetchAttachments(p, cookies);
	}

	/**
	 * Gets task messages.
	 * @param task Task.
	 * @return List of messages.
	 */
	public List<Message> getTaskMessages(Task task) throws IOException, ParseException,
			JSONException, OldCookiesException {
		return task.fetchMessages(p, cookies);
	}

	/**
	 * Uploads the attachment.
	 * @param t Task.
	 * @param f File.
	 * @return Attachment object.
	 */
	public Attachment uploadAttachment(Task t, File f) throws IOException, UnsupportedForlabsException, JSONException, OldCookiesException {
		return uploadAttachment(t, f.getName(), f.length(), getMimeType(f.toURI().toString()), new FileInputStream(f));
	}

	/**
	 * Uploads the attachment.
	 * @param t Task.
	 * @param fileName File name.
	 * @param fileLength Length of the file.
	 * @param mimeType MIME-type of the file.
	 * @param is Input stream.
	 * @return Attachment object.
	 */
	public Attachment uploadAttachment(Task t, String fileName, long fileLength, String mimeType, InputStream is)
			throws IOException, UnsupportedForlabsException, JSONException, OldCookiesException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(t.createAttachmentUrl()).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// read the page
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// get fresh cookies
		cookies.replaceBy(con);

		// parse that thing
		Document doc = p.parseInput(response.toString(), LOGIN_URL);

		// get elements with token
		Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();
		String token = elements.get(0).attr("value");

		// create filename only with letters and digits
		StringBuilder filename2 = new StringBuilder(fileName);
		for (int i = filename2.length() - 1; i >= 0; i--)
			if (!Character.isLetterOrDigit(filename2.charAt(i)))
				filename2.deleteCharAt(i);

		// setup connection
		con = (HttpURLConnection) new URL(UPLOAD_URL + UPLOAD_MASK.replace("${size}", Long.toString(fileLength))
				.replace("${filename}", URLEncoder.encode(fileName, "utf-8")).replace("${filename2}",
						URLEncoder.encode(filename2.toString(), "utf-8"))).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		con.addRequestProperty("X-CSRF-Token", token);
		cookies.putTo(con);

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// get some fresh cookies
		cookies.replaceBy(con);

		// setup connection
		con = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		con.addRequestProperty("X-CSRF-Token", token);
		cookies.putTo(con);

		// create random multipart divider
		String multipartDivider = "----------------";
		Random r = new Random();
		for (int i = 0; i < 16; i++)
			multipartDivider += Integer.toHexString(r.nextInt(16));
		con.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + multipartDivider);

		// open output stream
		OutputStream os = con.getOutputStream();
		PrintStream ps = new PrintStream(os, true, "utf-8");

		// multipart thing
		// write boundary
		ps.print("--");
		ps.println(multipartDivider);

		// flow chunk number
		ps.print("Content-Disposition: form-data; name=\"flowChunkNumber\"\n\n1\n--");
		ps.println(multipartDivider);

		// flow chunk size
		ps.print("Content-Disposition: form-data; name=\"flowChunkSize\"\n\n1048576\n--");
		ps.println(multipartDivider);

		// flow current chunk size
		ps.print("Content-Disposition: form-data; name=\"flowCurrentChunkSize\"\n\n");
		ps.print(fileLength);
		ps.print("\n--");
		ps.println(multipartDivider);

		// flow total size
		ps.print("Content-Disposition: form-data; name=\"flowTotalSize\"\n\n");
		ps.print(fileLength);
		ps.print("\n--");
		ps.println(multipartDivider);

		// flow identifier
		ps.print("Content-Disposition: form-data; name=\"flowIdentifier\"\n\n");
		ps.print(filename2);
		ps.print("\n--");
		ps.println(multipartDivider);

		// flow filename
		ps.print("Content-Disposition: form-data; name=\"flowFilename\"\n\n");
		ps.print(fileName);
		ps.print("\n--");
		ps.println(multipartDivider);

		// flow relative path
		ps.print("Content-Disposition: form-data; name=\"flowRelativePath\"\n\n");
		ps.print(fileName);
		ps.print("\n--");
		ps.println(multipartDivider);

		// flow total chunks
		ps.print("Content-Disposition: form-data; name=\"flowTotalChunks\"\n\n1\n--");
		ps.println(multipartDivider);

		// file
		ps.print("Content-Disposition: form-data; name=\"file\"; filename=\"");
		ps.print(fileName);
		ps.print("\"\nContent-Type: ");
		ps.print(mimeType != null ? mimeType : "application/octet-stream");
		ps.print("\n\n");

		// write binary data
		byte[] buf = new byte[4096];
		int read;
		while ((read = is.read(buf, 0, 4096)) > 0)
			os.write(buf, 0, read);
		is.close();

		// write last boundary
		ps.print("\n--");
		ps.print(multipartDivider);
		ps.println("--");
		ps.close();

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// get answer
		response = new StringBuilder();
		sc = new Scanner(con.getInputStream(), "utf-8");
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// get some fresh cookies
		cookies.replaceBy(con);

		// return attachment object
		return new Attachment(new JSONObject(response.toString()).getJSONObject("attachment"));
	}

	public void sendMessageToTask(Task t, String message, List<Attachment> attachments) throws IOException, UnsupportedForlabsException, JSONException, OldCookiesException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(t.createAttachmentUrl()).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// read the page
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		StringBuilder response = new StringBuilder();
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// get some fresh cookies
		cookies.replaceBy(con);

		// parse that thing
		Document doc = p.parseInput(response.toString(), LOGIN_URL);

		// get elements with token
		Elements elements = doc.body().getElementsByAttributeValue("name", "_token");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();
		String token = elements.get(0).attr("value");

		// setup connection
		con = (HttpURLConnection) new URL(t.createMessageUrl()).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.addRequestProperty("Content-Type", "application/json;charset=utf-8");
		con.addRequestProperty("User-Agent", USER_AGENT);
		con.addRequestProperty("X-Requested-With", "XMLHttpRequest");
		con.addRequestProperty("X-XSRF-Token", cookies.get("XSRF-TOKEN"));
		con.addRequestProperty("X-CSRF-Token", token);
		cookies.putTo(con);

		// create message object
		JSONObject json = new JSONObject();
		json.put("message", message);
		JSONArray attachmentsArr = new JSONArray();
		for (Attachment a : attachments)
			attachmentsArr.put(a.toJson());
		json.put("attachments", attachmentsArr);

		// send it!
		OutputStream os = con.getOutputStream();
		os.write(json.toString().getBytes("utf-8"));
		os.close();

		// if it's redirection, throw exception
		if (con.getResponseCode() == 302)
			throw new OldCookiesException();

		// get some fresh cookies
		cookies.replaceBy(con);
	}
}
