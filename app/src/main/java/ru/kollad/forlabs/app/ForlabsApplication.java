package ru.kollad.forlabs.app;

import android.app.Application;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 20.11.2018.
 */
public class ForlabsApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(this).getInt(Keys.THEME, -99) - 1);
	}
}
