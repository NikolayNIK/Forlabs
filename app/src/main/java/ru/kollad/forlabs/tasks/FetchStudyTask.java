package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import androidx.annotation.Nullable;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 16.11.2018.
 */
public class FetchStudyTask extends AsyncTask<Object, Void, Object> {

	private final OnPostExecuteListener listener;

	public FetchStudyTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Object doInBackground(Object... args) {
		File file = (File) args[0];
		Study study = (Study) args[1];

		try {
			Cookies cookies = (Cookies) SerializableUtil.read(file);

			API api = new API(cookies);
			study = api.fetchStudy(study);
			SerializableUtil.write(file, api.getCookies());
			return study;
		} catch (Exception e) {
			Log.w("Forlabs", "Unable to fetch study", e);
			return e;
		}
	}

	@Override
	protected void onPostExecute(Object o) {
		if (o instanceof Study) listener.onPostExecute(this, (Study) o, null);
		else listener.onPostExecute(this, null, (Throwable) o);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(FetchStudyTask task, @Nullable Study study, @Nullable Throwable cause);
	}
}
