package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.tasks.CheckUpdatesTask;

/**
 * Created by NikolayNIK on 12.02.2019.
 */
public class MainActivityViewModel extends ViewModel implements CheckUpdatesTask.OnPostExecuteListener {

	private static final String URL = "http://kollad.ru/products/forlabs/downloads/index.json";

	private final MutableLiveData<String> updateChangelog = new MutableLiveData<>();

	private JSONObject updateJson;

	public MutableLiveData<String> getUpdateChangelog() {
		return updateChangelog;
	}

	public JSONObject getUpdateJson() {
		return updateJson;
	}

	@Override
	public void onPostExecute(CheckUpdatesTask task, JSONObject json, String changelog) {
		updateJson = json;
		updateChangelog.setValue(changelog);
	}

	public void checkForUpdates(Context context) {
		new CheckUpdatesTask(this).execute(URL, new File(context.getExternalCacheDir(), "update.json"));
	}
}
