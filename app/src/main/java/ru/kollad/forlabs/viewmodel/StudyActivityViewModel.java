package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.tasks.FetchStudyTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 16.11.2018.
 */
public class StudyActivityViewModel extends ViewModel implements FetchStudyTask.OnPostExecuteListener {

	private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
	private final MutableLiveData<Study> study = new MutableLiveData<>();

	public MutableLiveData<Boolean> getRefreshing() {
		return refreshing;
	}

	public LiveData<Study> getStudy() {
		return study;
	}

	public void fetchStudy(Context context, Study emptyStudy) {
		refreshing.setValue(true);
		new FetchStudyTask(this).execute(Keys.getCookiesFile(context), emptyStudy);
	}

	@Override
	public void onPostExecute(FetchStudyTask task, @Nullable Study study, @Nullable Throwable cause) {
		refreshing.setValue(false);
		this.study.setValue(study);
	}
}
