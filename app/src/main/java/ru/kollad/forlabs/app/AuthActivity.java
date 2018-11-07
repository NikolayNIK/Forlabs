package ru.kollad.forlabs.app;

import androidx.appcompat.app.AppCompatActivity;
import ru.kollad.forlabs.R;

import android.os.Bundle;
import android.view.View;

import static android.view.View.*;

public class AuthActivity extends AppCompatActivity {

	private View inputEmail, inputPassword, buttonLogIn, buttonForgotPassword, progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		getWindow().setBackgroundDrawableResource(R.color.accent);

		inputEmail = findViewById(R.id.input_email);
		inputPassword = findViewById(R.id.input_password);
		buttonLogIn = findViewById(R.id.button_log_in);
		buttonForgotPassword = findViewById(R.id.button_forgot_password);
		progress = findViewById(R.id.progress);
	}

	private void form(int visibility) {
		inputEmail.setVisibility(visibility);
		inputPassword.setVisibility(visibility);
		buttonLogIn.setVisibility(visibility);
		buttonForgotPassword.setVisibility(visibility);
	}
}
