package ru.kollad.forlabs.tasks;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.Nullable;

/**
 * Created by NikolayNIK on 10.11.2018.
 */
public class DownloadJsonArrayTask extends DownloadTextTask<JSONArray> {

	private final OnPostExecuteListener listener;

	public DownloadJsonArrayTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected JSONArray handle(String string) throws JSONException {
		return new JSONArray(string);
	}

	@Override
	protected void onPostExecute(JSONArray jsonArray) {
		listener.onPostExecute(this, jsonArray);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(DownloadJsonArrayTask task, @Nullable JSONArray result);
	}
}
