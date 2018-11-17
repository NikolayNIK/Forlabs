package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.tasks.FetchAttachmentsTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class TaskDescriptionFragmentViewModel extends ViewModel implements FetchAttachmentsTask.OnPostExecuteListener {

	private final MutableLiveData<List<Attachment>> attachments = new MutableLiveData<>();

	public MutableLiveData<List<Attachment>> getAttachments() {
		return attachments;
	}

	@Override
	public void onPostExecute(FetchAttachmentsTask task, List<Attachment> attachments) {
		this.attachments.setValue(attachments);
	}

	public void fetchAttachments(Context context, Task task) {
		new FetchAttachmentsTask(this).execute(Keys.getCookiesFile(context), task);
	}
}
