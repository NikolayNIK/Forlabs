package ru.kollad.forlabs.app;

import androidx.appcompat.app.AppCompatActivity;
import ru.kollad.forlabs.R;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	static final String EXTRA_STUDENT_INFO = "studentInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
