package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import androidx.annotation.Nullable;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 09.11.2018.
 */
public class CheckCookiesTask extends AsyncTask<File, Void, StudentInfo> {

	private final OnPostExecuteListener listener;

	public CheckCookiesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected StudentInfo doInBackground(File... files) {
		try {
			Cookies cookies = (Cookies) SerializableUtil.read(files[0]);

			API api = new API(cookies);
			StudentInfo studentInfo = api.fetchDashboard();
			SerializableUtil.write(files[0], api.getCookies());
			return studentInfo;
		} catch (Exception e) {
			Log.d("Forlabs", "Exception reading cookies", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(StudentInfo studentInfo) {
		listener.onPostExecute(this, studentInfo);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(CheckCookiesTask task, @Nullable StudentInfo studentInfo);
	}
}
