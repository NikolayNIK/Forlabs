package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_dashboard, container, false);
	}

}
