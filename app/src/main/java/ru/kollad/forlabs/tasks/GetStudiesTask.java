package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Studies;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 12.11.2018.
 */
public class GetStudiesTask extends AsyncTask<File, Void, Studies> implements Comparator<Study> {

	private static final long CACHE_INVALIDATION_TIME_MILLIS = 5 * 60 * 1000;

	private final OnPostExecuteListener listener;

	public GetStudiesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Studies doInBackground(File... files) {
		try {
			File cacheFile = files[1];
			if (System.currentTimeMillis() - cacheFile.lastModified() > CACHE_INVALIDATION_TIME_MILLIS) {
				try {
					File cookiesFile = files[0];
					API api = new API((Cookies) SerializableUtil.read(cookiesFile));
					Studies studies = api.getStudies();
					for (Study study : studies)
						api.fetchStudy(study);

					Collections.sort(studies, this);

					SerializableUtil.write(cookiesFile, api.getCookies());
					SerializableUtil.write(cacheFile, studies);
					return studies;
				} catch (Exception e) {
					Log.i("Forlabs", "Unable to download studies", e);
				}
			}

			return (Studies) SerializableUtil.read(cacheFile);
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to acquire studies", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Studies studies) {
		listener.onPostExecute(this, studies);
	}

	@Override
	public int compare(Study o1, Study o2) {
		int tmp = o1.getStatus() - o2.getStatus();
		return tmp == 0 ? (int) Math.signum(o1.getPoints() - o2.getPoints()) : tmp;
	}

	public interface OnPostExecuteListener {

		void onPostExecute(GetStudiesTask task, Studies studies);
	}
}
