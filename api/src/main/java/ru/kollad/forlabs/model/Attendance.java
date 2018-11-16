package ru.kollad.forlabs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Represents attendance on the lesson.
 */
public class Attendance implements Serializable {
	private final int id;
	private final Date date;
	private final double cost;
	private final String comment;
	private final boolean isPresent;

	/**
	 * Constructor for JSON.
	 * @param json JSON.
	 */
	public Attendance(JSONObject json) throws ParseException, JSONException {
		id = json.optInt("id", 0);
		date = new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("date", "1970-01-01"));
		cost = json.optDouble("cost", 0);
		comment = json.optString("comment", "");
		isPresent = json.getJSONObject("pivot").optInt("is_present", 0) == 1;
	}

	public int getId() {
		return id;
	}
	public Date getDate() {
		return date;
	}
	public double getCost() {
		return cost;
	}
	public String getComment() {
		return comment;
	}
	public boolean isPresent() {
		return isPresent;
	}

	@NonNull
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%d: %s %s", id, isPresent ? "был" : "не был", date.toString());
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Attendance)) return false;

		Attendance a2 = (Attendance) obj;
		return id == a2.id;
	}
	@Override
	public int hashCode() {
		return id ^ date.hashCode() ^ (int) cost ^ comment.hashCode() ^ (isPresent ? 1 : 0);
	}
}
