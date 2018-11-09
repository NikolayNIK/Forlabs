package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 09.11.2018.
 */
public class LogOutTask extends AsyncTask<File, Void, Boolean> {

	private final OnPostExecuteListener listener;

	public LogOutTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(File... files) {
		try {
			API api = new API((Cookies) SerializableUtil.read(files[0]));
			api.logout();
			return files[0].delete();
		} catch (Exception e) {
			Log.d("Forlabs", "Could not log out", e);
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		listener.onPostExecute(this, aBoolean);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(LogOutTask task, boolean success);
	}
}
