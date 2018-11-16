package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.kollad.forlabs.model.Study;

/**
 * Created by NikolayNIK on 16.11.2018.
 */
public class StudyFragment extends Fragment {

	private static final String KEY_STUDY = "study";

	private Study study;

	StudyFragment setStudy(Study study) {
		this.study = study;
		this.onStudyChanged();
		return this;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null)
			study = (Study) savedInstanceState.getSerializable(KEY_STUDY);

		onStudyChanged(view, study);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_STUDY, study);
	}

	private void onStudyChanged() {
		View view = getView();
		if (view != null) onStudyChanged(view, study);
	}

	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {

	}
}
