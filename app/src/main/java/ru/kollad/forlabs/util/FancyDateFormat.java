package ru.kollad.forlabs.util;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import ru.kollad.forlabs.R;

/**
 * Created by NikolayNIK on 20.11.2018.
 */
public class FancyDateFormat {

	private static Calendar kostil;

	public static String date(Context context, Date date) {
		if (kostil == null) kostil = Calendar.getInstance();
		kostil.setTime(date);
		return context.getString(R.string.format_date,
				kostil.get(Calendar.DAY_OF_MONTH),
				context.getResources().getStringArray(R.array.months)[kostil.get(Calendar.MONTH)],
				kostil.get(Calendar.YEAR));
	}

	public static String datetime(Context context, Date date) {
		if (kostil == null) kostil = Calendar.getInstance();
		kostil.setTime(date);
		return context.getString(R.string.format_date_time,
				kostil.get(Calendar.DAY_OF_MONTH),
				context.getResources().getStringArray(R.array.months)[kostil.get(Calendar.MONTH)],
				kostil.get(Calendar.YEAR),
				kostil.get(Calendar.HOUR_OF_DAY),
				kostil.get(Calendar.MINUTE));
	}
}
