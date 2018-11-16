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

	private Study study;

	StudyFragment setStudy(Study study) {
		this.study = study;
		this.onStudyReady();
		return this;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onStudyReady(view, study);
	}

	private void onStudyReady() {
		View view = getView();
		if (view != null) onStudyReady(view, study);
	}

	protected void onStudyReady(@NonNull View view, @Nullable Study study) {

	}
}
