package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Studies;
import ru.kollad.forlabs.tasks.GetStudiesTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 12.11.2018.
 */
public class MainStudiesFragmentViewModel extends ViewModel implements GetStudiesTask.OnPostExecuteListener {

	private final MutableLiveData<Studies> studies = new MutableLiveData<>();

	public MutableLiveData<Studies> getStudies() {
		return studies;
	}

	public void fetchStudies(Context context) {
		new GetStudiesTask(this).execute(Keys.getCookiesFile(context), Keys.getStudiesFile(context));
	}

	@Override
	public void onPostExecute(GetStudiesTask task, Studies studies) {
		this.studies.setValue(studies);
	}
}
