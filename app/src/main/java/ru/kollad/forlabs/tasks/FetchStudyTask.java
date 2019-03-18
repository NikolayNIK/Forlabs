package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 16.11.2018.
 */
public class FetchStudyTask extends AsyncTask<Object, Void, Object> {

	private static final long CACHE_INVALIDATION_TIME_MILLIS = 5 * 60 * 1000;

	private final OnPostExecuteListener listener;

	public FetchStudyTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Object doInBackground(Object... args) {
		File fileCookies = (File) args[0];
		File fileStudy = (File) args[1];
		Study study;
		boolean ignoreCache = (boolean) args[3];

		try {
			File parent = fileStudy.getParentFile();
			if (!parent.isDirectory() && !parent.mkdirs())
				throw new IOException("Unable to mkdirs: " + parent);

			if (ignoreCache ||
					System.currentTimeMillis() - fileStudy.lastModified() > CACHE_INVALIDATION_TIME_MILLIS ||
					(study = (Study) SerializableUtil.tryRead(fileStudy)) == null) {
				Cookies cookies = (Cookies) SerializableUtil.read(fileCookies);

				API api = new API(cookies);
				study = api.fetchStudy((Study) args[2]);
				SerializableUtil.write(fileCookies, api.getCookies());
				SerializableUtil.write(fileStudy, study);
			}

			return study;
		} catch (Exception e) {
			Log.w("Forlabs", "Unable to fetch study", e);

			study = (Study) SerializableUtil.tryRead(fileStudy);
			if (study != null) return study;

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
