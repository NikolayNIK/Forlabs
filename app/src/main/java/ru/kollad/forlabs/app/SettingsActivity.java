package ru.kollad.forlabs.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.util.Keys;

public class SettingsActivity extends AppCompatActivity {

	private SharedPreferences prefs;
	private ViewGroup layoutSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		layoutSettings = findViewById(R.id.layout_settings);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_daynight_24dp);
		}

		inflateListSelectorItem(Keys.THEME, R.string.pref_theme_title, R.string.pref_theme_subtitle, R.array.pref_theme, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppCompatDelegate.setDefaultNightMode(which - 1);
				if (which == 1 && Build.VERSION.SDK_INT >= 23 &&
						checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
			}
		});

		inflateListSelectorItem(Keys.DEFAULT_SECTION, R.string.pref_default_title, R.string.pref_default_subtitle, R.array.pref_default, 0, null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("SameParameterValue")
	private View inflateItem(@LayoutRes int layout, @StringRes int title, @StringRes int subtitle) {
		View view = getLayoutInflater().inflate(layout, layoutSettings, false);
		((TextView) view.findViewById(R.id.text_title)).setText(title);
		((TextView) view.findViewById(R.id.text_subtitle)).setText(subtitle);
		return view;
	}

	private View inflateSimpleItem(@StringRes int title, @StringRes int subtitle) {
		return inflateItem(R.layout.activity_settings_item, title, subtitle);
	}

	private void inflateListSelectorItem(final String key, final @StringRes int title, @StringRes int subtitle, final @ArrayRes int list, final int defValue, @Nullable final DialogInterface.OnClickListener listener) {
		View view = inflateSimpleItem(title, subtitle);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
				adb.setTitle(title);
				adb.setSingleChoiceItems(list, prefs.getInt(key, defValue), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (listener != null) listener.onClick(dialog, which);
						prefs.edit().putInt(key, which).apply();
						dialog.dismiss();
					}
				});

				adb.show();
			}
		});

		layoutSettings.addView(view);
	}
}
