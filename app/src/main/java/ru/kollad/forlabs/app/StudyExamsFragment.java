package ru.kollad.forlabs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;

/**
 * Created by NikolayNIK on 19.11.2018.
 */
public class StudyExamsFragment extends StudyFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_exams, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable final Study study) {
		final View buttonWebsite = view.findViewById(R.id.button_website);
		if (study == null) {
			buttonWebsite.setVisibility(View.GONE);
			buttonWebsite.setOnClickListener(null);
		} else {
			buttonWebsite.setVisibility(View.VISIBLE);
			buttonWebsite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://forlabs.ru/studies/" + study.getId() + "#exams")));
				}
			});
		}
	}
}
