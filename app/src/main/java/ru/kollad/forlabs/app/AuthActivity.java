package ru.kollad.forlabs.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.viewmodel.AuthActivityViewModel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import static android.view.View.*;

public class AuthActivity extends AppCompatActivity implements Observer<Cookies> {

	private static final Uri URI_FORGOT_PASSWORD = Uri.parse("https://forlabs.ru/password/reset");

	private AuthActivityViewModel model;
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

		model = ViewModelProviders.of(this).get(AuthActivityViewModel.class);
		model.getCookies().observe(this, this);
		if (model.isChecked() == null) {
			model.checkCookies(this);
		} else if (model.isChecked()) {
			Cookies cookies = model.getCookies().getValue();
			if (cookies == null) {
				form(VISIBLE);
			} else {
				proceed(cookies);
			}
		} else {
			progress(VISIBLE);
		}

		buttonLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				form(GONE);
				progress(VISIBLE);

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
	public void onChanged(@Nullable Cookies cookies) {
		progress(GONE);
		if (cookies == null) {
			progress.post(new Runnable() {
				@Override
				public void run() {
					form(VISIBLE);
				}
			});
		} else {
			proceed(cookies);
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

	private void proceed(Cookies cookies) {
		startActivity(new Intent(this, MainActivity.class));
	}
}
