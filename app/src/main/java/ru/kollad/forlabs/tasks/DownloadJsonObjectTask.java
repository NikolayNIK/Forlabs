package ru.kollad.forlabs.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;

/**
 * Created by NikolayNIK on 10.11.2018.
 */
public class DownloadJsonObjectTask extends DownloadTextTask<JSONObject> {

	private final OnPostExecuteListener listener;

	public DownloadJsonObjectTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected JSONObject handle(String string) throws JSONException {
		return new JSONObject(string);
	}

	@Override
	protected void onPostExecute(JSONObject jsonArray) {
		listener.onPostExecute(this, jsonArray);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(DownloadJsonObjectTask task, @Nullable JSONObject result);
	}
}
