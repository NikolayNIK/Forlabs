package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class FetchAttachmentsTask extends AsyncTask<Object, Void, List<Attachment>> {

	private final OnPostExecuteListener listener;

	public FetchAttachmentsTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<Attachment> doInBackground(Object... args) {
		File file = (File) args[0];
		Task task = (Task) args[1];

		try {
			Cookies cookies = (Cookies) SerializableUtil.read(file);

			API api = new API(cookies);
			List<Attachment> attachments = api.getTaskAttachments(task);
			SerializableUtil.write(file, api.getCookies());
			return attachments;
		} catch (Exception e) {
			Log.w("Forlabs", "Unable to fetch study", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<Attachment> attachments) {
		listener.onPostExecute(this, attachments);
	}

	public interface OnPostExecuteListener {

		void onPostExecute(FetchAttachmentsTask task, List<Attachment> attachments);
	}
}
