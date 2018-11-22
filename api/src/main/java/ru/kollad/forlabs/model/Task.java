package ru.kollad.forlabs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.api.exceptions.CaptchaException;
import ru.kollad.forlabs.api.exceptions.OldCookiesException;
import ru.kollad.forlabs.api.exceptions.UnsupportedForlabsException;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Represents a task.
 */
public class Task implements Serializable {
	/** URL for fetching attachments */
	private static final String ATTACHMENT_URL = "https://forlabs.ru/studies/${studyId}/tasks/${id}";
	/** URL for fetching messages */
	private static final String MESSAGES_URL = "https://forlabs.ru/studies/${studyId}/tasks/${id}/responses";

	private final int id;
	private final int studyId;
	private final int status;
	private final String name;
	private int sort;
	private final String content;
	private final String instructions;
	private final double cost;
	private Date createdAt;
	private Date updatedAt;
	private Date deletedAt;

	private Assignment assignment;
	private List<Attachment> attachments;
	private List<Message> messages;

	/**
	 * Constructor for JSON.
	 * @param json JSON.
	 */
	public Task(JSONObject json) throws ParseException, JSONException {
		id = json.optInt("id", 0);
		name = json.optString("name", "");
		content = json.optString("content", "");
		instructions = json.optString("instructions", "");
		JSONObject pivot = json.getJSONObject("pivot");
		studyId = pivot.optInt("study_id", 0);
		cost = pivot.optDouble("cost", 0);
		status = pivot.optInt("status", 0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		if (!json.isNull("created_at")) createdAt = sdf.parse(json.optString("created_at", "1970-01-01 00:00:00"));
		if (!json.isNull("updated_at")) updatedAt = sdf.parse(json.optString("updated_at", "1970-01-01 00:00:00"));
		if (!json.isNull("deleted_at")) deletedAt = sdf.parse(json.optString("deleted_at", "1970-01-01 00:00:00"));
	}

	public int getId() {
		return id;
	}
	public int getStudyId() {
		return studyId;
	}
	public int getStatus() {
		return status;
	}
	public String getName() {
		return name;
	}
	public int getSort() {
		return sort;
	}
	public String getContent() {
		return content;
	}
	public String getInstructions() {
		return instructions;
	}
	public double getCost() {
		return cost;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public Date getDeletedAt() {
		return deletedAt;
	}
	public Assignment getAssignment() {
		return assignment;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Fetch some attachments.
	 * @param p Parser for parsing HTMLs.
	 * @param cookies Cookies for getting new one.
	 * @return List of attachments.
	 */
	public List<Attachment> fetchAttachments(Parser p, Cookies cookies) throws IOException,
			UnsupportedForlabsException, JSONException, OldCookiesException, CaptchaException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection)
				new URL(createAttachmentUrl()).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", API.USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		int code = con.getResponseCode();
		if (code == 404)
			throw new CaptchaException();
		if (code == 302)
			throw new OldCookiesException();

		// read the page
		StringBuilder response = new StringBuilder();
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		while (sc.hasNextLine()) {
			response.append(sc.nextLine());
			response.append("\n");
		}
		sc.close();

		// get some fresh cookies
		cookies.replaceBy(con);

		// parse the page
		Document doc = p.parseInput(response.toString(), con.getURL().toString());
		Elements elements = doc.getElementsByClass("container-fluid");
		if (elements.size() == 0)
			throw new UnsupportedForlabsException();

		// init reader
		sc = new Scanner(elements.get(0).child(0).childNode(0).toString());
		sc.nextLine();
		sc.nextLine();

		// get json
		String jsonStr = sc.nextLine();
		sc.close();
		JSONObject task = new JSONObject(jsonStr.substring(jsonStr.indexOf('{'), jsonStr.lastIndexOf(';')));
		sort = task.optInt("sort", 0);
		JSONArray jsonArr = task.getJSONArray("attachments");

		// parse json to array list
		attachments = new ArrayList<>();
		for (int i = 0; i < jsonArr.length(); i++)
			attachments.add(new Attachment(jsonArr.getJSONObject(i)));

		return attachments;
	}

	/**
	 * Fetch some messages.
	 * @param cookies Cookies for getting new one.
	 * @return List of messages.
	 */
	public List<Message> fetchMessages(Cookies cookies) throws IOException, ParseException,
			JSONException, OldCookiesException, CaptchaException {
		// setup connection
		HttpURLConnection con = (HttpURLConnection)
				new URL(MESSAGES_URL.replace("${studyId}", Integer.toString(studyId))
						.replace("${id}", Integer.toString(id))).openConnection();
		con.setInstanceFollowRedirects(false);
		con.setDoInput(true);
		con.addRequestProperty("User-Agent", API.USER_AGENT);
		cookies.putTo(con);

		// if it's redirection, throw exception
		int code = con.getResponseCode();
		if (code == 404)
			throw new CaptchaException();
		if (code == 302)
			throw new OldCookiesException();

		// read the page
		StringBuilder response = new StringBuilder();
		Scanner sc = new Scanner(con.getInputStream(), "utf-8");
		while (sc.hasNextLine())
			response.append(sc.nextLine());
		sc.close();

		// getting the fresh cookies
		cookies.replaceBy(con);

		// read all messages
		JSONArray mesArr = new JSONArray(response.toString());
		messages = new ArrayList<>();
		for (int i = 0; i < mesArr.length(); i++)
			messages.add(new Message(mesArr.getJSONObject(i)));

		return messages;
	}

	/**
	 * Sets new assignments.
	 * @param assignment Assignment.
	 */
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	/**
	 * Creates an attachments URL.
	 * @return URL.
	 */
	public String createAttachmentUrl() {
		return ATTACHMENT_URL.replace("${studyId}", Integer.toString(studyId))
				.replace("${id}", Integer.toString(id));
	}

	/**
	 * Creates a messages URL.
	 * @return URL.
	 */
	public String createMessageUrl() {
		return MESSAGES_URL.replace("${studyId}", Integer.toString(studyId))
				.replace("${id}", Integer.toString(id));
	}

	/**
	 * Represents an assignment.
	 */
	public static class Assignment implements Serializable {
		public static final int STATUS_DEBT = 1;
		public static final int STATUS_SENT = 2;
		public static final int STATUS_SUCCESS = 3;
		public static final int STATUS_GOT_ANSWER = 6;
		public static final int STATUS_HAS_QUESTIONS = 7;
		public static final int STATUS_DEFAULT = 0;

		private final int id;
		private final int taskId;
		private final int status;

		private Date lastRepliedAt;
		private Date updatedAt;
		private Assessment assessment;

		/**
		 * Constructor for JSON.
		 * @param json JSON.
		 */
		public Assignment(JSONObject json) throws ParseException, JSONException {
			id = json.optInt("id", 0);
			taskId = json.optInt("task_id", 0);
			status = json.optInt("status", 0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
			if (!json.isNull("last_replied_at")) lastRepliedAt = sdf.parse(json.optString("last_replied_at", "1970-01-01 00:00:00"));
			if (!json.isNull("updated_at")) updatedAt = sdf.parse(json.optString("updated_at", "1970-01-01 00:00:00"));

			if (!json.isNull("assessment"))
				assessment = new Assessment(json.getJSONObject("assessment"));
		}

		public int getId() {
			return id;
		}
		public int getTaskId() {
			return taskId;
		}
		public int getStatus() {
			return status;
		}
		public Date getLastRepliedAt() {
			return lastRepliedAt;
		}
		public Date getUpdatedAt() {
			return updatedAt;
		}
		public Assessment getAssessment() {
			return assessment;
		}
	}

	/**
	 * Represents an assessment.
	 */
	public static class Assessment implements Serializable {
		private final int id;
		private final int taskId;
		private final int type;
		private final double credits;
		private final String comment;

		private final String cause;
		private final int status;
		private Date createdAt;
		private Date lastRepliedAt;

		private Date updatedAt;

		/**
		 * Constructor for JSON.
		 * @param json JSON.
		 */
		public Assessment(JSONObject json) throws ParseException, JSONException {
			id = json.optInt("id", 0);
			type = json.optInt("type", 0);
			credits = json.optDouble("credits", 0);
			comment = json.optString("comment", "");
			cause = json.optString("cause", "");
			if (!json.isNull("date")) createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(json.optString("date", "1970-01-01"));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
			JSONObject creditable = json.getJSONObject("creditable");
			taskId = creditable.optInt("task_id", 0);
			if (!creditable.isNull("last_replied_at")) lastRepliedAt = sdf.parse(creditable.optString("last_replied_at", "1970-01-01 00:00:00"));
			if (!creditable.isNull("updated_at")) updatedAt = sdf.parse(creditable.optString("updated_at", "1970-01-01 00:00:00"));
			status = creditable.optInt("status", 0);
		}

		public int getId() {
			return id;
		}
		public int getTaskId() {
			return taskId;
		}
		public int getType() {
			return type;
		}
		public double getCredits() {
			return credits;
		}
		public String getComment() {
			return comment;
		}
		public String getCause() {
			return cause;
		}
		public int getStatus() {
			return status;
		}
		public Date getCreatedAt() {
			return createdAt;
		}
		public Date getLastRepliedAt() {
			return lastRepliedAt;
		}
		public Date getUpdatedAt() {
			return updatedAt;
		}
	}
}
