package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class FetchMessagesTask extends AsyncTask<Object, Void, List<Message>> {

	private final OnPostExecuteListener listener;

	public FetchMessagesTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<Message> doInBackground(Object... args) {
		File file = (File) args[0];
		Task task = (Task) args[1];
		try {
			Cookies cookies = (Cookies) SerializableUtil.read(file);

			List<Message> messages = task.fetchMessages(cookies);
			SerializableUtil.write(file, cookies);
			return messages;
		} catch (Exception e) {
			Log.w("Forlabs", "Unable to fetch messages", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<Message> messages) {
		listener.onPostExecute(this, messages);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(FetchMessagesTask task, List<Message> messages);
	}
}
