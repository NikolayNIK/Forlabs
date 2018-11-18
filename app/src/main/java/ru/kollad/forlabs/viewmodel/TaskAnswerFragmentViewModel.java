package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.tasks.FetchMessagesTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class TaskAnswerFragmentViewModel extends ViewModel implements FetchMessagesTask.OnPostExecuteListener {

	private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();

	public MutableLiveData<List<Message>> getMessages() {
		return messages;
	}

	public void fetchMessages(Context context, Task task) {
		new FetchMessagesTask(this).execute(Keys.getCookiesFile(context), task);
	}

	@Override
	public void onPostExecute(FetchMessagesTask task, List<Message> messages) {
		this.messages.setValue(messages);
	}
}
