package ru.kollad.forlabs.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class Keys {

	// TODO Put SharedPreferences keys here

	private static File getDir(Context context) {
		return Build.VERSION.SDK_INT >= 21 ? context.getNoBackupFilesDir() : context.getFilesDir();
	}

	public static File getCookiesFile(Context context) {
		return new File(getDir(context), "cookies");
	}

	public static File getStudentInfoFile(Context context) {
		return new File(getDir(context), "studentInfo");
	}

	public static File getStudiesFile(Context context) {
		return new File(getDir(context), "studies");
	}

	public static File getScheduleDirectory(Context context) {
		File dir = context.getExternalCacheDir();
		if (dir == null || !dir.isDirectory()) dir = context.getCacheDir();
		dir = new File(dir, "schedule");
		if (!dir.isDirectory() && !dir.mkdirs()) Log.e("Forlabs", "Unable to create schedule directory. Something went totally wrong!");
		return dir;
	}

	public static File getScheduleIndexFile(Context context) {
		return new File(getScheduleDirectory(context), "index.json");
	}
}
