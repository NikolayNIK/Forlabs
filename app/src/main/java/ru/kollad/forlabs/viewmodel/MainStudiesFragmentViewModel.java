package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Semesters;
import ru.kollad.forlabs.tasks.GetStudiesTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 12.11.2018.
 */
public class MainStudiesFragmentViewModel extends ViewModel implements GetStudiesTask.OnPostExecuteListener {

	private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
	private final MutableLiveData<Semesters> semesters = new MutableLiveData<>();

	private int selectedSemester;

	public MutableLiveData<Boolean> getRefreshing() {
		return refreshing;
	}

	public MutableLiveData<Semesters> getStudies() {
		return semesters;
	}

	public int getSelectedSemester() {
		return selectedSemester;
	}

	public void setSelectedSemester(int selectedSemester) {
		this.selectedSemester = selectedSemester;
	}

	public void fetchStudies(Context context, boolean ignoreCache) {
		refreshing.setValue(true);
		new GetStudiesTask(this).execute(Keys.getCookiesFile(context), Keys.getStudiesFile(context), ignoreCache);
	}

	@Override
	public void onPostExecute(GetStudiesTask task, Semesters semesters) {
		refreshing.setValue(false);
		this.semesters.setValue(semesters);
	}
}
