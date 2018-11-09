package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.tasks.CheckCookiesTask;
import ru.kollad.forlabs.tasks.LogInTask;
import ru.kollad.forlabs.tasks.ReadSerializableTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class AuthActivityViewModel extends ViewModel implements
		CheckCookiesTask.OnPostExecuteListener,
		LogInTask.Callback {

	/**
	 * null - check never started.
	 * 0 - check started.
	 * 1 - enter credentials.
	 * 2 - login.
	 * 3 - login error.
	 * 4 - ready.
	 */
	private final MutableLiveData<Integer> state = new MutableLiveData<>();

	private StudentInfo studentInfo;
	private Throwable cause;

	public MutableLiveData<Integer> getState() {
		return state;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public Throwable getCause() {
		return cause;
	}

	public void checkCookies(Context context) {
		File file = Keys.getCookiesFile(context);
		if (file.exists()) {
			state.setValue(0);
			new CheckCookiesTask(this).execute(file);
		} else {
			state.setValue(1);
		}
	}

	public void checkCredentials(Context context, String email, String password) {
		state.setValue(2);
		new LogInTask(this).execute(Keys.getCookiesFile(context), Keys.getStudentInfoFile(context), email, password);
	}

	@Override
	public void onPostExecute(CheckCookiesTask task, @Nullable StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
		this.state.setValue(studentInfo == null ? 1 : 4);
	}

	@Override
	public void onLogInSuccess(LogInTask task, Cookies cookies, StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
		this.state.setValue(4);
	}

	@Override
	public void onLogInFailure(LogInTask task, Throwable cause) {
		this.cause = cause;
		this.state.setValue(3);
	}
}
