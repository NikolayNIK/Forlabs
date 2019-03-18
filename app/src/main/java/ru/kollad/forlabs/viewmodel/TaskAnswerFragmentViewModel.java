package ru.kollad.forlabs.viewmodel;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.tasks.FetchMessagesTask;
import ru.kollad.forlabs.tasks.SendMessageTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class TaskAnswerFragmentViewModel extends ViewModel implements
		FetchMessagesTask.OnPostExecuteListener,
		SendMessageTask.OnPostExecuteListener {

	private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();

	public MutableLiveData<List<Message>> getMessages() {
		return messages;
	}

	public void fetchMessages(Context context, Task task, boolean ignoreCache) {
		new FetchMessagesTask(this).execute(Keys.getCookiesFile(context), new File(context.getExternalCacheDir(), "messages/" + task.getId()), task, ignoreCache);
	}

	public void sendMessage(Context context, Task task, String text, List<Uri> attachments) {
		new SendMessageTask(this).execute(context.getApplicationContext(), task, text, attachments);
	}

	@Override
	public void onPostExecute(FetchMessagesTask task, List<Message> messages) {
		this.messages.setValue(messages);
	}

	@Override
	public void onPostExecute(SendMessageTask task, List<Message> messages) {
		this.messages.setValue(messages);
	}
}
