package ru.kollad.forlabs.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.kollad.forlabs.BuildConfig;

/**
 * Created by NikolayNIK on 12.02.2019.
 */
public class CheckUpdatesTask extends DownloadTextTask<Object[]> {

	private final OnPostExecuteListener listener;

	public CheckUpdatesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Object[] handle(String string) throws JSONException {
		JSONArray array = new JSONArray(string);
		StringBuilder sb = new StringBuilder();

		int maxCode = BuildConfig.VERSION_CODE;
		JSONObject max = null;
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			int vc = object.getInt("versionCode");
			if (vc > maxCode) {
				max = object;
				maxCode = vc;
				sb.append(object.getString("changelog"));
				sb.append("\n\n");
			}
		}

		return max == null ? null : new Object[]{max, sb.toString().trim()};
	}

	@Override
	protected void onPostExecute(Object[] objects) {
		if (objects == null || objects.length < 2) return;
		listener.onPostExecute(this, (JSONObject) objects[0], (String) objects[1]);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(CheckUpdatesTask task, JSONObject json, String changelog);
	}
}
