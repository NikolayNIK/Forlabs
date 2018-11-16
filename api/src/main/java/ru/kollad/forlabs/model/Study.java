package ru.kollad.forlabs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.api.exceptions.OldCookiesException;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;

/**
 * Represents a study.
 */
public class Study implements Serializable {
	/**
	 * URL for fetching info
	 */
	private static final String FETCH_URL = "https://forlabs.ru/studies/${id}";

	private static final long serialVersionUID = 7171310035332698041L;

	/**
	 * Statuses
	 */
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_DEBT = -1;
	public static final int STATUS_CERTIFIED = 1;

	private int id;
	private List<Integer> lecturersId;
	private String name;

	private float points;
	private int grade;
	private float attendPercent;
	private List<Attendance> attendances;

	private String teacherName;
	private String simpleTeacherName;
	private int status;

	private List<Task> tasks;

	private List<ScheduleItem> scheduleItems;

	/**
	 * Constructor for page element.
	 *
	 * @param html Page element.
	 */
	public Study(Element html) {
		Elements elements = html.child(0).children();

		// block A
		Elements block = elements.get(0).children();
		String url = block.get(0).attr("href");
		id = Integer.parseInt(url.substring(url.lastIndexOf('/') + 1));
		name = block.get(0).child(0).text();
		points = Float.parseFloat(block.get(1).text().replace(',', '.'));

		simpleTeacherName = elements.get(1).text();
		block = elements.get(1).children();
		if (block.size() == 2)
			status = "аттестован".equals(block.get(1).attr("title")) ? STATUS_CERTIFIED : STATUS_DEBT;
	}

	/**
	 * Fetch some info about study.
	 *
	 * @param p       Parser for parsing HTMLs.
	 * @param cookies Cookies for updating.
	 * @return Study, but with more info!
	 */
	public Study fetch(Parser p, Cookies cookies) throws IOException, UnsupportedForlabsException, ParseException, JSONException, OldCookiesException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection) new URL(FETCH_URL.replace("${id}", Integer.toString(id))).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", API.USER_AGENT);
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

		// parse document
		Document doc = p.parseInput(response.toString(), con.getURL().toString());

		// get teacher's name
		Elements elements = doc.getElementsByClass("dl-horizontal");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();
		teacherName = elements.get(0).child(6).text();

		// get secret container
		elements = doc.getElementsByClass("container-fluid");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();

		// init reader
		sc = new Scanner(elements.get(0).child(0).childNode(0).toString());
		sc.nextLine();

		// perform study object
		String jsonStr = sc.nextLine();
		JSONObject json = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));

		// lecturers
		lecturersId = new ArrayList<>();
		lecturersId.add(json.optInt("lecturer_id", 0));

		int lecturerId = json.optInt("lecturer2_id", 0);
		if (lecturerId != 0) lecturersId.add(lecturerId);

		lecturerId = json.optInt("lecturer3_id", 0);
		if (lecturerId != 0) lecturersId.add(lecturerId);

		// parse tasks
		tasks = new ArrayList<>();
		JSONArray jsonArr = json.getJSONArray("tasks");
		for (int i = 0; i < jsonArr.length(); i++)
			tasks.add(new Task(jsonArr.getJSONObject(i)));

		// skip student info
		sc.nextLine();

		// get score object
		jsonStr = sc.nextLine();
		json = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));

		// fill score fields
		points = (float) json.optDouble("credits", 0);
		grade = json.optInt("grade");

		// skip assessments
		sc.nextLine();

		// get task assignments
		jsonStr = sc.nextLine();
		jsonStr = jsonStr.substring(jsonStr.indexOf('=') + 2, jsonStr.lastIndexOf(';'));
		if (jsonStr.startsWith("{")) {
			json = new JSONObject(jsonStr);

			// fill assessments
			Iterator<String> keys = json.keys();
			while (keys.hasNext()) {
				Task.Assignment ta = new Task.Assignment(json.getJSONObject(keys.next()));
				for (Task t : tasks)
					if (t.getId() == ta.getTaskId()) {
						t.setAssignment(ta);
						break;
					}
			}
		}

		// skip attend percent (we will calculate it)
		sc.nextLine();

		// get attendances
		jsonStr = sc.nextLine();
		jsonArr = new JSONArray(jsonStr.substring(jsonStr.indexOf('['), jsonStr.lastIndexOf(';')));

		// fill it!
		attendances = new ArrayList<>();
		float isPresentCount = 0;
		for (int i = 0; i < jsonArr.length(); i++) {
			Attendance a = new Attendance(jsonArr.getJSONObject(i));
			isPresentCount += a.isPresent() ? 1 : 0;
			attendances.add(a);
		}
		attendPercent = (int) (isPresentCount / jsonArr.length() * 100);

		// get schedule items
		jsonStr = sc.nextLine();
		jsonArr = new JSONArray(jsonStr.substring(jsonStr.indexOf('['), jsonStr.lastIndexOf(';')));

		// fill it!
		scheduleItems = new ArrayList<>();
		for (int i = 0; i < jsonArr.length(); i++)
			scheduleItems.add(new ScheduleItem(jsonArr.getJSONObject(i)));

		// close the stream
		sc.close();

		return this;
	}

	public int getId() {
		return id;
	}

	public List<Integer> getLecturersId() {
		return lecturersId;
	}

	public String getName() {
		return name;
	}

	public float getPoints() {
		return points;
	}

	public int getGrade() {
		return grade;
	}

	public float getAttendPercent() {
		return attendPercent;
	}

	public List<Attendance> getAttendances() {
		return attendances;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public String getSimpleTeacherName() {
		return simpleTeacherName;
	}

	public int getStatus() {
		return status;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public List<ScheduleItem> getScheduleItems() {
		return scheduleItems;
	}
}
