package ru.kollad.forlabs.app;

import androidx.appcompat.app.AppCompatActivity;
import ru.kollad.forlabs.R;

import android.os.Bundle;

public class StudyActivity extends AppCompatActivity {

	static final String EXTRA_STUDIES = "studies";
	static final String EXTRA_STUDY = "study";
	static final String EXTRA_STUDY_NAME = "study_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
	}
}
