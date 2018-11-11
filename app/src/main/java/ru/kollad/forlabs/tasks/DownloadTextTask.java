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

	@Override
	protected T doInBackground(Object... args) {
		try {
			StringBuilder sb = new StringBuilder();

			try {
				int length;
				byte[] buffer = new byte[(int) (Runtime.getRuntime().freeMemory() / 16)];
				InputStream input = new URL((String) args[0]).openStream();
				try {
					OutputStream output = new FileOutputStream((File) args[1]);
					try {
						while ((length = input.read(buffer)) > 0) {
							output.write(buffer, 0, length);
							sb.append(new String(buffer, 0, length));
						}
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

			if (sb.length() == 0) {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader((File) args[1]));
				while ((line = reader.readLine()) != null)
					sb.append(line);
				reader.close();
			}

			return handle(sb.toString());
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to fetch " + args[0], e);
			return null;
		}
	}

	protected abstract T handle(String string) throws Exception;
}
