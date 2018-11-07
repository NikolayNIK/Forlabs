package ru.kollad.forlabs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Message {
	private int id;
	private String userName;
	private String message;
	private Date createdAt;
	private List<Attachment> attachments;

	public Message(JSONObject json) throws ParseException, JSONException {
		id = json.optInt("id", 0);
		message = json.optString("message", "");
		createdAt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(json.optString("created_at", "1970-01-01 00:00:00"));
		userName = json.getJSONObject("user").optString("name", "<unknown>");

		attachments = new ArrayList<>();
		JSONArray attachmentsArr = json.getJSONArray("attachments");
		for (int i = 0; i < attachmentsArr.length(); i++)
			attachments.add(new Attachment(attachmentsArr.getJSONObject(i)));
	}

	public int getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%s: %s (%d attachments)", userName, message, attachments.size());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Message)) return false;

		Message m2 = (Message) obj;
		return id == m2.id;
	}

	@Override
	public int hashCode() {
		return id ^ userName.hashCode() ^ message.hashCode() ^ createdAt.hashCode() ^ attachments.hashCode();
	}
}
