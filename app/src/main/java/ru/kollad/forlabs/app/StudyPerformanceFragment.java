package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Assessment;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.FancyDateFormat;

/**
 * Created by NikolayNIK on 19.11.2018.
 */
public class StudyPerformanceFragment extends StudyFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_performance, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {
		View cardScore = view.findViewById(R.id.card_score);
		View cardAssessments = view.findViewById(R.id.card_assessments);
		if (study == null) {
			cardScore.setVisibility(View.GONE);
			cardAssessments.setVisibility(View.GONE);
		} else {
			cardScore.setVisibility(View.VISIBLE);
			cardAssessments.setVisibility(View.VISIBLE);

			((TextView) cardScore.findViewById(R.id.text_score)).setText(getString(R.string.text_study_performance_score, study.getPoints()));
			((ProgressBar) cardScore.findViewById(R.id.progress_score)).setProgress(Math.min(100, Math.round(study.getPoints())));

			ViewGroup layoutAssessments = cardAssessments.findViewById(R.id.layout_assessments);
			for (Assessment assessment : study.getAssessments()) {
				View viewAss = getLayoutInflater().inflate(R.layout.fragment_study_performance_assessment, layoutAssessments, false);
				((TextView) viewAss.findViewById(R.id.text_name)).setText(assessment.getCause());
				((TextView) viewAss.findViewById(R.id.text_date)).setText(FancyDateFormat.date(getContext(), assessment.getDate()));
				((TextView) viewAss.findViewById(R.id.text_score)).setText(getString(R.string.text_score, assessment.getCredits()));
				layoutAssessments.addView(viewAss);
			}
		}
	}
}
