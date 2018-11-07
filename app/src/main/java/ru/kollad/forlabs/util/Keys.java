package ru.kollad.forlabs.util;

import android.content.Context;
import android.os.Build;

import java.io.File;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class Keys {

	// TODO Put SharedPreferences keys here

	public static File getCookiesFile(Context context) {
		return new File(Build.VERSION.SDK_INT >= 21 ? context.getNoBackupFilesDir() : context.getFilesDir(), "cookies");
	}
}
