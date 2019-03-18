package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;

/**
 * Created by NikolayNIK on 13.11.2018.
 */
public class ReportTask extends AsyncTask<String, Void, Exception> {

	private static final String URL = "http://kollad.ru/products/forlabs/issues/new.php";

	private final OnPostExecuteListener listener;

	public ReportTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Exception doInBackground(String... strings) {
		try {
			byte[] bytes = strings[0].getBytes("UTF-8");

			HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setFixedLengthStreamingMode(bytes.length);
			connection.connect();

			connection.getOutputStream().write(bytes);
			connection.getOutputStream().close();

			if (connection.getResponseCode() != 200)
				throw new IOException("Invalid response sending report");

			connection.disconnect();

			return null;
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to report issue", e);
			return e;
		}
	}

	@Override
	protected void onPostExecute(@Nullable Exception exception) {
		listener.onPostExecute(this, exception);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(ReportTask task, @Nullable Exception exception);
	}
}
