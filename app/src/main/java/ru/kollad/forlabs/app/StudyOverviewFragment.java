package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;

/**
 * Created by NikolayNIK on 14.11.2018.
 */
public class StudyOverviewFragment extends StudyFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_overview, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {
		View cardOverview = view.findViewById(R.id.card_overview);
		if (study == null) {
			cardOverview.setVisibility(View.GONE);
		} else {
			cardOverview.setVisibility(View.VISIBLE);

			((TextView) cardOverview.findViewById(R.id.text_score)).setText(getString(R.string.text_score, study.getPoints()));
			((TextView) cardOverview.findViewById(R.id.text_attendance)).setText(getString(R.string.text_attendance, Math.round(study.getAttendPercent())));
			if (study.getStatus() == Study.STATUS_NORMAL) {
				cardOverview.findViewById(R.id.text_status).setVisibility(View.GONE);
				cardOverview.findViewById(R.id.text_status_title).setVisibility(View.GONE);
				cardOverview.findViewById(R.id.image_status).setVisibility(View.GONE);
			} else if (study.getStatus() == Study.STATUS_CERTIFIED) {
				((TextView) cardOverview.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_certified));
				((ImageView) cardOverview.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_thumb_up_24dp);
			} else {
				((TextView) cardOverview.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_debt));
				((ImageView) cardOverview.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_warning_24dp);
			}

			((TextView) view.findViewById(R.id.text_teacher)).setText(study.getSimpleTeacherName());
		}
	}
}
