package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.tasks.DownloadJsonArrayTask;
import ru.kollad.forlabs.tasks.DownloadJsonObjectTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 10.11.2018.
 */
public class MainScheduleFragmentViewModel extends ViewModel implements DownloadJsonArrayTask.OnPostExecuteListener, DownloadJsonObjectTask.OnPostExecuteListener {

	private final MutableLiveData<JSONArray> index = new MutableLiveData<>();
	private final MutableLiveData<JSONObject> schedule = new MutableLiveData<>();

	private int course = -1, stream = -1, week = -1;

	private String scheduleLink;

	public LiveData<JSONArray> getIndex() {
		return index;
	}

	public MutableLiveData<JSONObject> getSchedule() {
		return schedule;
	}

	public int getCourse() {
		return course;
	}

	public int getStream() {
		return stream;
	}

	public int getWeek() {
		return week;
	}

	public void setCourse(int course) {
		this.course = course;
	}

	public void setStream(int stream) {
		this.stream = stream;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	@Override
	public void onPostExecute(DownloadJsonArrayTask task, @Nullable JSONArray result) {
		index.setValue(result == null ? new JSONArray() : result);
	}

	@Override
	public void onPostExecute(DownloadJsonObjectTask task, @Nullable JSONObject result) {
		schedule.setValue(result);
	}

	public void fetchIndex(Context context) {
		new DownloadJsonArrayTask(this).execute("https://forlabs.ru/api/v1/rasp", Keys.getScheduleIndexFile(context));
	}

	public void fetchSchedule(Context context, String link) {
		if (scheduleLink == null || !scheduleLink.equals(link)) {
			scheduleLink = link;
			schedule.setValue(null);
			new DownloadJsonObjectTask(this).execute(link, new File(Keys.getScheduleDirectory(context), link.substring(link.lastIndexOf("/") + 1) + ".json"));
		} else {
			schedule.setValue(schedule.getValue());
		}
	}
}
