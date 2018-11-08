package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;

import java.io.File;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class LogInTask extends AsyncTask<Object, Void, Object> {

	private final Callback callback;

	public LogInTask(Callback callback) {
		this.callback = callback;
	}

	@Override
	protected Object doInBackground(Object... objects) {
		File cookiesFile = (File) objects[0];
		File studentInfoFile = (File) objects[1];
		String email = (String) objects[2];
		String password = (String) objects[3];
		try {
			API api = new API();
			api.login(email, password);

			SerializableUtil.write(cookiesFile, api.getCookies());
			SerializableUtil.write(studentInfoFile, api.getStudentInfo());

			return api;
		} catch (Throwable e) {
			return e;
		}
	}

	@Override
	protected void onPostExecute(Object o) {
		if (o instanceof API) {
			API api = (API) o;
			callback.onLogInSuccess(this, api.getCookies(), api.getStudentInfo());
		} else {
			callback.onLogInFailure(this, (Throwable) o);
		}
	}

	public interface Callback {

		void onLogInSuccess(LogInTask task, Cookies cookies, StudentInfo studentInfo);

		void onLogInFailure(LogInTask task, Throwable cause);
	}
}
