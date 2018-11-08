package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import ru.kollad.forlabs.model.Cookies;

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
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(files[0]));
			try { // Try-with-resources requires API level 19 (current min is 17)
				cookies = (Cookies) stream.readObject();
			} finally {
				stream.close();
			}
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
