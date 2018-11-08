package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class ReadSerializableTask extends AsyncTask<File, Void, Object[]> {

	private final OnPostExecuteListener listener;

	public ReadSerializableTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Object[] doInBackground(File... files) {
		Object[] result = new Object[files.length];
		for (int i = 0; i < result.length; i++) {
			try {
				result[i] = SerializableUtil.read(files[i]);
			} catch (Exception e) {
				Log.d("Forlabs", "Exception reading serializable", e);
			}
		}

		return result;
	}

	@Override
	protected void onPostExecute(Object[] result) {
		listener.onPostExecute(this, result);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(ReadSerializableTask task, @NonNull Object[] result);
	}
}
