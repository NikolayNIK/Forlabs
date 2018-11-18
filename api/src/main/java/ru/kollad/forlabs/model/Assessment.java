package ru.kollad.forlabs.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Assessment {
	public final int id;
	public final double credits;
	public final String comment;
	public final String cause;
	public Date date;

	public Assessment(JSONObject json) throws ParseException {
		id = json.optInt("id", 0);
		credits = json.optDouble("credits", 0);
		comment = json.optString("comment", "");
		cause = json.optString("cause", "");
		if (!json.isNull("date")) date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.parse(json.optString("date", "1970-01-01"));
	}

	public long getId() {
		return id;
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
	public Date getDate() {
		return date;
	}
}
