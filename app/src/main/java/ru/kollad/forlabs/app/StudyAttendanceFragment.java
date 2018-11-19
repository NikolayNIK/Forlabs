package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Attendance;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.FancyDateFormat;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class StudyAttendanceFragment extends StudyFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_attendance, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {
		View cardOverview = view.findViewById(R.id.card_overview);
		View cardAttendance = view.findViewById(R.id.card_attendance);
		View textEmpty = view.findViewById(R.id.text_empty);
		if (study == null) {
			cardOverview.setVisibility(View.GONE);
			cardAttendance.setVisibility(View.GONE);
			textEmpty.setVisibility(View.GONE);
		} else if (study.getAttendances().isEmpty()) {
			cardOverview.setVisibility(View.GONE);
			cardAttendance.setVisibility(View.GONE);
			textEmpty.setVisibility(View.VISIBLE);
		} else {
			cardOverview.setVisibility(View.VISIBLE);
			cardAttendance.setVisibility(View.VISIBLE);
			textEmpty.setVisibility(View.GONE);

			((TextView) cardOverview.findViewById(R.id.text_attendance)).setText(getString(R.string.text_study_attendance_value, study.getAttendPercent()));
			((ProgressBar) cardOverview.findViewById(R.id.progress_attendance)).setProgress(Math.min(100, Math.round(study.getAttendPercent())));

			ViewGroup layoutAttendance = cardAttendance.findViewById(R.id.layout_attendance);
			for (Attendance attendance : study.getAttendances()) {
				View viewAttendance = getLayoutInflater().inflate(R.layout.fragment_study_attendance_item, layoutAttendance, false);
				((TextView) viewAttendance.findViewById(R.id.text_date))
						.setText(FancyDateFormat.date(view.getContext(), attendance.getDate()));

				((ImageView) viewAttendance.findViewById(R.id.image_status))
						.setImageResource(attendance.isPresent() ?
								R.drawable.ic_done_daynight_24dp :
								R.drawable.ic_close_daynight_24dp);

				layoutAttendance.addView(viewAttendance);
			}
		}
	}
}
