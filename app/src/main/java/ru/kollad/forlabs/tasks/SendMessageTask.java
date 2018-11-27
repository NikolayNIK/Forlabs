package ru.kollad.forlabs.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.documentfile.provider.DocumentFile;
import ru.kollad.forlabs.api.API;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Cookies;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.DocumentFileUtil;
import ru.kollad.forlabs.util.Keys;
import ru.kollad.forlabs.util.SerializableUtil;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class SendMessageTask extends AsyncTask<Object, Void, List<Message>> {

	private final OnPostExecuteListener listener;

	public SendMessageTask(OnPostExecuteListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<Message> doInBackground(Object... args) {
		Context applicationContext = (Context) args[0];
		File file = Keys.getCookiesFile(applicationContext);
		Task task = (Task) args[1];
		String message = (String) args[2];
		List<Uri> uris = (List<Uri>) args[3];
		try {
			API api = new API((Cookies) SerializableUtil.read(file));

			ContentResolver contentResolver = applicationContext.getContentResolver();
			List<Attachment> attachments = new ArrayList<>(uris.size());
			for (int i = 0; i < uris.size(); i++) {
				Uri uri = uris.get(i);
				DocumentFile documentFile = DocumentFileUtil.fromUri(applicationContext, uri);
				if (documentFile != null) {
					String name = documentFile.getName();
					if (name != null) {
						long length = documentFile.length();
						String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substring(name.lastIndexOf(".") + 1));

						InputStream input = contentResolver.openInputStream(uri);
						if (input != null) {
							attachments.add(api.uploadAttachment(task, name, length, mimeType == null ? "*/*" : mimeType, input));
							input.close();
						}
					}
				}
			}

			api.sendMessageToTask(task, message, attachments);
			List<Message> messages = task.fetchMessages(api.getCookies());
			SerializableUtil.write(file, api.getCookies());
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

		void onPostExecute(SendMessageTask task, List<Message> messages);
	}
}
