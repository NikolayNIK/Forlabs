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
import ru.kollad.forlabs.model.ScheduleItem;
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
		View cardSchedule = view.findViewById(R.id.card_schedule);
		if (study == null) {
			cardOverview.setVisibility(View.GONE);
			cardSchedule.setVisibility(View.GONE);
		} else {
			int attendance = Math.round(study.getAttendPercent());

			cardOverview.setVisibility(View.VISIBLE);
			((TextView) cardOverview.findViewById(R.id.text_score)).setText(getString(R.string.text_score, study.getPoints()));

			TextView textAttendance = cardOverview.findViewById(R.id.text_attendance);
			ProgressBar progressAttendance = cardOverview.findViewById(R.id.progress_attendance);
			if (study.getAttendances().isEmpty()) {
				textAttendance.setText(R.string.text_study_overview_attendance_no);
				progressAttendance.setVisibility(View.GONE);
			} else {
				textAttendance.setText(getString(R.string.text_attendance, attendance));
				progressAttendance.setProgress(attendance);
			}

			if (study.getStatus() == Study.STATUS_NORMAL) {
				cardOverview.findViewById(R.id.text_status).setVisibility(View.GONE);
				cardOverview.findViewById(R.id.text_status_title).setVisibility(View.GONE);
				cardOverview.findViewById(R.id.image_status).setVisibility(View.GONE);
			} else if (study.getStatus() == Study.STATUS_CERTIFIED) {
				((TextView) cardOverview.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_certified));
				((ImageView) cardOverview.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_thumb_up_accent_24dp);
			} else {
				((TextView) cardOverview.findViewById(R.id.text_status)).setText(getString(R.string.text_study_overview_status_debt));
				((ImageView) cardOverview.findViewById(R.id.image_status)).setImageResource(R.drawable.ic_warning_accent_24dp);
			}

			((TextView) view.findViewById(R.id.text_teacher)).setText(study.getTeacherName());


			cardSchedule.setVisibility(View.VISIBLE);
			ViewGroup layoutSchedule = cardSchedule.findViewById(R.id.layout_schedule);
			for (ScheduleItem item : study.getScheduleItems()) {
				View viewDay = getLayoutInflater().inflate(R.layout.fragment_study_overview_schedule, layoutSchedule, false);
				((TextView) viewDay.findViewById(R.id.text_day)).setText(item.getDayName());
				((TextView) viewDay.findViewById(R.id.text_time)).setText(item.getTime());
				((TextView) viewDay.findViewById(R.id.text_room)).setText(item.getRoom().getName());

				layoutSchedule.addView(viewDay);
			}
		}
	}
}
