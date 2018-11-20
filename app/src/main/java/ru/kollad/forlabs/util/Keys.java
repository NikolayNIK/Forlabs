package ru.kollad.forlabs.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class Keys {

	public static final String THEME = "theme";
	public static final String DEFAULT_SECTION = "default";

	private static File getDataDir(Context context) {
		return Build.VERSION.SDK_INT >= 21 ? context.getNoBackupFilesDir() : context.getFilesDir();
	}

	private static File getCacheDir(Context context) {
		File dir = context.getExternalCacheDir();
		if (dir == null || !dir.isDirectory())
			return context.getCacheDir();
		return dir;
	}

	public static File getCookiesFile(Context context) {
		return new File(getDataDir(context), "cookies");
	}

	public static File getStudentInfoFile(Context context) {
		return new File(getCacheDir(context), "studentInfo");
	}

	public static File getStudiesFile(Context context) {
		return new File(getCacheDir(context), "studies");
	}

	public static File getScheduleDirectory(Context context) {
		File dir = new File(getCacheDir(context), "schedule");
		if (!dir.isDirectory() && !dir.mkdirs())
			Log.e("Forlabs", "Unable to create schedule directory. Something went totally wrong!");
		return dir;
	}

	public static File getScheduleIndexFile(Context context) {
		return new File(getScheduleDirectory(context), "index.json");
	}
}
