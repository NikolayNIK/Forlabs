package ru.kollad.forlabs.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Attachments;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class FetchAttachmentsTask extends AsyncTask<Object, Void, List<Attachment>> {

	private static final long CACHE_INVALIDATION_TIME_MILLIS = 5 * 60 * 1000;

	private final OnPostExecuteListener listener;

	public FetchAttachmentsTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<Attachment> doInBackground(Object... args) {
		File fileCookies = (File) args[0];
		File fileAttachments = (File) args[1];
		Task task = (Task) args[2];
		boolean ignoreCache = (boolean) args[3];

		try {
			if (ignoreCache || System.currentTimeMillis() - fileAttachments.lastModified() > CACHE_INVALIDATION_TIME_MILLIS) {
				try {
					File parent = fileAttachments.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs())
						throw new IOException("Unable to mkdirs: " + parent);

					Cookies cookies = (Cookies) SerializableUtil.read(fileCookies);

					API api = new API(cookies);
					Attachments attachments = api.getTaskAttachments(task);
					SerializableUtil.write(fileCookies, api.getCookies());
					SerializableUtil.write(fileAttachments, attachments);
					return attachments;
				} catch (Exception e) {
					Log.w("Forlabs", "Unable to fetch attachments", e);
					return null;
				}
			}

			return (Attachments) SerializableUtil.read(fileAttachments);
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to acquire attachments", e);
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
