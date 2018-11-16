package ru.kollad.forlabs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Represents the schedule item for study.
 */
public class ScheduleItem implements Serializable {
	private final int id;
	private final int day;
	private final int position;
	private final int type;
	private final int status;
	private final String dayName;
	private final String time;
	private Room room;

	/**
	 * Constructor for JSON.
	 * @param json JSON.
	 */
	public ScheduleItem(JSONObject json) throws JSONException {
		id = json.optInt("id", 0);
		day = json.optInt("day", 0);
		position = json.optInt("position", 0);
		type = json.optInt("type", 0);
		status = json.optInt("status", 0);
		dayName = json.optString("day_name", "");
		time = json.optString("time", "");
		if (!json.isNull("room")) room = new Room(json.getJSONObject("room"));
	}

	public int getId() {
		return id;
	}
	public int getDay() {
		return day;
	}
	public int getPosition() {
		return position;
	}
	public int getType() {
		return type;
	}
	public int getStatus() {
		return status;
	}
	public String getDayName() {
		return dayName;
	}
	public String getTime() {
		return time;
	}
	public Room getRoom() {
		return room;
	}

	/**
	 * Represents a room where study goes.
	 */
	public class Room implements Serializable {
		private final int id;
		private final String code;
		private final String name;
		private final String address;

		/**
		 * Constructor for JSON.
		 * @param json JSON.
		 */
		public Room(JSONObject json) {
			id = json.optInt("id", 0);
			code = json.optString("code", "");
			name = json.optString("name", "");
			address = json.optString("address", "");
		}

		public int getId() {
			return id;
		}
		public String getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public String getAddress() {
			return address;
		}
	}
}
