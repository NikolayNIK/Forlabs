package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import java.io.File;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.tasks.CheckCookiesTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class AuthActivityViewModel extends ViewModel implements CheckCookiesTask.OnPostExecuteListener {

	private final MutableLiveData<Cookies> cookies = new MutableLiveData<>();

	/**
	 * null - check never started.
	 * false - started.
	 * true - finished.
	 */
	private Boolean isChecked;

	public LiveData<Cookies> getCookies() {
		return cookies;
	}

	public Boolean isChecked() {
		return isChecked;
	}

	public void checkCookies(Context context) {
		isChecked = false;
		File file = Keys.getCookiesFile(context);
		if (file.exists()) {
			new CheckCookiesTask(this).execute(file);
		} else {
			onPostExecute(null, null);
		}
	}

	@Override
	public void onPostExecute(CheckCookiesTask task, Cookies result) {
		isChecked = true;
		cookies.setValue(result);
	}
}
