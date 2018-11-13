package ru.kollad.forlabs.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import ru.kollad.forlabs.R;

public class ReportActivity extends AppCompatActivity implements TextWatcher {

	private EditText editTitle, editMessage;
	private View buttonSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		editTitle = findViewById(R.id.edit_title);
		editMessage = findViewById(R.id.edit_message);
		buttonSend = findViewById(R.id.button_send);
		TextInputLayout inputMessage = findViewById(R.id.input_message);
		CheckBox checkStacktrace = findViewById(R.id.check_stacktrace);

		String stacktrace = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
		if (stacktrace == null) {
			editTitle.addTextChangedListener(this);
			editMessage.addTextChangedListener(this);
			inputMessage.setHint(getString(R.string.hint_report_message));
			buttonSend.setEnabled(false);
			findViewById(R.id.text_crash).setVisibility(View.GONE);

			if (getSupportActionBar() != null) {
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_daynight_24dp);
			}
		} else {
			editTitle.setText(R.string.text_report_crash_title);
			editTitle.setEnabled(false);
			inputMessage.setHint(getString(R.string.hint_report_message_unnecessary));
			checkStacktrace.setChecked(true);
		}

		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getSupportFragmentManager().beginTransaction()
						.add(ReportFragment.newInstance(formReport()), null)
						.commit();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		buttonSend.setEnabled(!TextUtils.isEmpty(editTitle.getText()) && !TextUtils.isEmpty(editMessage.getText()));
	}

	private String formReport() {
		try {
			JSONObject object = new JSONObject();
			try {
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
				object.put("app_version_code", info == null ? null : info.versionCode);
				object.put("app_version_name", info == null ? null : info.versionName);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}

			object.put("android_sdk", Build.VERSION.SDK_INT);
			object.put("stacktrace", CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

			EditText editEmail = findViewById(R.id.edit_email);
			if (!TextUtils.isEmpty(editEmail.getText()))
				object.put("email", editEmail.getText());
			object.put("title", editTitle.getText());
			if (!TextUtils.isEmpty(editMessage.getText()))
				object.put("message", editMessage.getText());

			return object.toString(2);
		} catch (JSONException e) {
			return e.toString();
		}
	}
}
