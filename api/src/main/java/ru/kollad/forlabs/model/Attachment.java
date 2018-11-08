package ru.kollad.forlabs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Represents an attachment.
 */
public class Attachment implements Serializable {
	private int id;
	private int type;
	private int status;
	private String uuid;
	private String fileName;
	private String mimeType;
	private long fileSize;
	private String humanFileSize;
	private String url;
	private String previewUrl;

	/**
	 * Constructor for JSON.
	 * @param json JSON.
	 */
	public Attachment(JSONObject json) {
		id = json.optInt("id", 0);
		type = json.optInt("type", 0);
		status = json.optInt("status", 0);
		uuid = json.optString("uuid", "");
		fileName = json.optString("filename", "");
		mimeType = json.optString("mime_type", "");
		fileSize = json.optInt("size", 0);
		humanFileSize = json.optString("human_size");
		url = json.optString("url", "");
		previewUrl = json.optString("preview", "");
	}

	/**
	 * Returns JSON representation.
	 * @return JSON.
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("type", type);
		json.put("status", status);
		json.put("uuid", uuid);
		json.put("filename", fileName);
		json.put("mime_type", mimeType);
		json.put("size", fileSize);
		json.put("human_size", humanFileSize);
		json.put("url", url);
		json.put("preview", previewUrl);

		return json;
	}

	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public int getStatus() {
		return status;
	}
	public String getUuid() {
		return uuid;
	}
	public String getFileName() {
		return fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public long getFileSize() {
		return fileSize;
	}
	public String getHumanFileSize() {
		return humanFileSize;
	}
	public String getUrl() {
		return url;
	}
	public String getPreviewUrl() {
		return previewUrl.startsWith("/") ? "https://forlabs.ru" + previewUrl : previewUrl;
	}

	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%d: %s", id, fileName);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Attachment)) return false;

		Attachment a2 = (Attachment) obj;
		return id == a2.id;
	}
	@Override
	public int hashCode() {
		return id ^ type ^ status ^ uuid.hashCode() ^ fileName.hashCode() ^ mimeType.hashCode() ^ (int) fileSize ^
				humanFileSize.hashCode() ^ url.hashCode() ^ previewUrl.hashCode();
	}
}