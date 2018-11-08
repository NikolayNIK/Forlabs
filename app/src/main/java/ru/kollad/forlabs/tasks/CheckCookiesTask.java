package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class CheckCookiesTask extends AsyncTask<File, Void, Cookies> {

	private final OnPostExecuteListener listener;

	public CheckCookiesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Cookies doInBackground(File... files) {
		Cookies cookies = null;
		try {
			cookies = (Cookies) SerializableUtil.read(files[0]);
		} catch (Exception e) {
			Log.d("Forlabs", "Exception eating cookie", e);
		}

		return cookies;
	}

	@Override
	protected void onPostExecute(Cookies cookies) {
		listener.onPostExecute(this, cookies);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(CheckCookiesTask task, Cookies result);
	}
}
