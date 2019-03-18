package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.StudentInfo;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class MainDashboardFragment extends MainFragment {

	private static final String KEY_STUDENT_INFO = "studentInfo";

	static MainDashboardFragment newInstance(StudentInfo info) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_STUDENT_INFO, info);

		MainDashboardFragment fragment = new MainDashboardFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.setRetainInstance(false);
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_dashboard, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		assert getArguments() != null;

		final StudentInfo studentInfo = (StudentInfo) getArguments().getSerializable(KEY_STUDENT_INFO);
		assert studentInfo != null;

		getToolbar().setTitle(studentInfo.getGroupName());

		final ViewGroup table = view.findViewById(R.id.table_dashboard);

		if(savedInstanceState == null) {
			Handler handler = new Handler();
			handler.post(() -> createA(table, studentInfo));

			handler.post(() -> createB(table, studentInfo));
		} else {
			createA(table, studentInfo);
			createB(table, studentInfo);
		}
	}

	private void createA(ViewGroup table, StudentInfo studentInfo) {
		((TextView) table.findViewById(R.id.text_direction)).setText(studentInfo.getDirectionName());
		((TextView) table.findViewById(R.id.text_profile)).setText(studentInfo.getProfileName());
		((TextView) table.findViewById(R.id.text_qualification)).setText(studentInfo.getQualificationName());
		((TextView) table.findViewById(R.id.text_form)).setText(studentInfo.getStudyFormName());
	}

	private void createB(ViewGroup table, StudentInfo studentInfo) {
		((TextView) table.findViewById(R.id.text_year)).setText(getString(R.string.text_dashboard_year_content, studentInfo.getAdmissionYear()));
		((TextView) table.findViewById(R.id.text_course)).setText(getString(R.string.text_dashboard_course_content, studentInfo.getCurrentCourse()));
		ProgressBar progressCourse = table.findViewById(R.id.progress_course);
		progressCourse.setMax(studentInfo.getTotalCourseCount());
		progressCourse.setProgress(studentInfo.getCurrentCourse());
	}
}
