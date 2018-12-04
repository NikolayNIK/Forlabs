package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by NikolayNIK on 10.11.2018.
 */
public abstract class DownloadTextTask<T> extends AsyncTask<Object, Void, T> {

	private static final long CACHE_INVALIDATION_TIME_MILLIS = 60 * 60 * 1000;

	@Override
	protected T doInBackground(Object... args) {
		try {
			File file = (File) args[1];

			T result;
			if (System.currentTimeMillis() - file.lastModified() >= CACHE_INVALIDATION_TIME_MILLIS || (result = tryHandle(file)) == null) {
				try {
					int length;
					byte[] buffer = new byte[(int) (Runtime.getRuntime().freeMemory() / 16)];
					InputStream input = new URL((String) args[0]).openStream();
					try {
						OutputStream output = new FileOutputStream(file);
						try {
							while ((length = input.read(buffer)) > 0)
								output.write(buffer, 0, length);
						} catch (Exception e) {
							output.close();
							throw e;
						}
						output.close();
					} catch (Exception e) {
						input.close();
						throw e;
					}
					input.close();
				} catch (Exception e) {
					Log.i("Forlabs", "Unable to download " + args[0], e);
				}

				result = handle(file);
			}

			return result;
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to fetch " + args[0], e);
			return null;
		}
	}

	private T tryHandle(File file) {
		try {
			return handle(file);
		} catch (Exception e) {
			Log.e("Forlabs", "Got some crap into the cached " + file, e);
			return null;
		}
	}

	private T handle(File file) throws Exception {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null)
			sb.append(line);
		reader.close();

		return handle(sb.toString());
	}

	protected abstract T handle(String string) throws Exception;
}
