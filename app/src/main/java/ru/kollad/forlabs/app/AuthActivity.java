package ru.kollad.forlabs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.api.exceptions.IncorrectCredentialsException;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.viewmodel.AuthActivityViewModel;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class AuthActivity extends AppCompatActivity implements Observer<Integer> {

	private static final Uri URI_FORGOT_PASSWORD = Uri.parse("https://forlabs.ru/password/reset");

	private AuthActivityViewModel model;
	private View inputEmail, buttonLogIn, buttonForgotPassword, progress;
	private EditText editEmail, editPassword;
	private TextInputLayout inputPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		getWindow().setBackgroundDrawableResource(R.color.background);

		inputEmail = findViewById(R.id.input_email);
		inputPassword = findViewById(R.id.input_password);
		editEmail = inputEmail.findViewById(R.id.edit_email);
		editPassword = inputPassword.findViewById(R.id.edit_password);
		buttonLogIn = findViewById(R.id.button_log_in);
		buttonForgotPassword = findViewById(R.id.button_forgot_password);
		progress = findViewById(R.id.progress);

		model = ViewModelProviders.of(this).get(AuthActivityViewModel.class);
		model.getState().observe(this, this);
		if (model.getState().getValue() == null) model.checkCookies(this);
		else handleState(model.getState().getValue());

		buttonLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.checkCredentials(AuthActivity.this, editEmail.getText().toString(), editPassword.getText().toString());
			}
		});

		buttonForgotPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, URI_FORGOT_PASSWORD));
			}
		});
	}

	@Override
	public void onChanged(final @Nullable Integer state) {
		if (state != null) {
			progress.post(new Runnable() {
				@Override
				public void run() {
					handleState(state);
				}
			});
		}
	}

	private void handleState(int state) {
		switch (state) {
			case 0:
				form(GONE);
				progress(VISIBLE);
				break;
			case 1:
				form(VISIBLE);
				progress(GONE);
				break;
			case 2:
				form(GONE);
				progress(VISIBLE);
				break;
			case 3:
				inputPassword.setError(model.getCause() instanceof IncorrectCredentialsException ?
						getString(R.string.error_auth_incorrect_credentials) :
						model.getCause().toString());
				model.getState().setValue(1);
				break;
			case 4:
				proceed(model.getStudentInfo());
				break;
		}
	}

	private void form(int visibility) {
		inputEmail.setVisibility(visibility);
		inputPassword.setVisibility(visibility);
		buttonLogIn.setVisibility(visibility);
		buttonForgotPassword.setVisibility(visibility);
	}

	private void progress(int visibility) {
		progress.setVisibility(visibility);
	}

	private void proceed(StudentInfo studentInfo) {
		startActivity(new Intent(this, MainActivity.class)
				.putExtra(MainActivity.EXTRA_STUDENT_INFO, studentInfo));
		finish();
	}
}
