package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Messages;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class FetchMessagesTask extends AsyncTask<Object, Void, Messages> {

	private static final long CACHE_INVALIDATION_TIME_MILLIS = 1000;

	private final OnPostExecuteListener listener;

	public FetchMessagesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected Messages doInBackground(Object... args) {
		File fileCookies = (File) args[0];
		File fileMessages = (File) args[1];
		Task task = (Task) args[2];
		boolean ignoreCache = (boolean) args[3];
		try {
			if (ignoreCache || System.currentTimeMillis() - fileMessages.lastModified() > CACHE_INVALIDATION_TIME_MILLIS) {
				try {
					File parent = fileMessages.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs())
						throw new IOException("Unable to mkdirs: " + parent);

					Cookies cookies = (Cookies) SerializableUtil.read(fileCookies);

					Messages messages = task.fetchMessages(cookies);
					SerializableUtil.write(fileCookies, cookies);
					SerializableUtil.write(fileMessages, messages);
				} catch (Exception e) {
					Log.e("Forlabs", "Unable to fetch messages", e);
				}
			}

			return (Messages) SerializableUtil.read(fileMessages);
		} catch (Exception e) {
			Log.w("Forlabs", "Unable to acquire messages", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Messages messages) {
		listener.onPostExecute(this, messages);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(FetchMessagesTask task, List<Message> messages);
	}
}
