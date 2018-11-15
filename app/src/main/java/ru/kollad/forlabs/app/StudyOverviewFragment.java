package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;

/**
 * Created by NikolayNIK on 14.11.2018.
 */
public class StudyOverviewFragment extends Fragment {

	private static final String KEY_STUDY = "study";

	static StudyOverviewFragment newInstance(Study study) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_STUDY, study);

		StudyOverviewFragment fragment = new StudyOverviewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		assert getArguments() != null;
		Study study = (Study) getArguments().getSerializable(KEY_STUDY);
		assert study != null;

		((TextView) view.findViewById(R.id.text_score)).setText(getString(R.string.text_score, study.getPoints()));
		((TextView) view.findViewById(R.id.text_attendance)).setText(getString(R.string.text_attendance, Math.round(study.getAttendPercent())));
		if (study.getStatus() == Study.STATUS_NORMAL) {
			view.findViewById(R.id.text_status).setVisibility(View.GONE);
			view.findViewById(R.id.text_status_title).setVisibility(View.GONE);
			view.findViewById(R.id.image_status).setVisibility(View.GONE);
		} else if (study.getStatus() == Study.STATUS_CERTIFIED) {
			((TextView) view.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_certified));
			((ImageView) view.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_thumb_up_24dp);
		} else {
			((TextView) view.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_debt));
			((ImageView) view.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_warning_24dp);
		}

		((TextView) view.findViewById(R.id.text_teacher)).setText(study.getSimpleTeacherName());
	}
}
