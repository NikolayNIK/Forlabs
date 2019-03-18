package ru.kollad.forlabs.viewmodel;

import android.content.Context;

import java.io.File;
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
		this.attachments.setValue(null);
		this.attachments.setValue(attachments);
	}

	public void fetchAttachments(Context context, MutableLiveData<Integer> counter, Task task, boolean ignoreCache) {
		new FetchAttachmentsTask(this, counter).execute(Keys.getCookiesFile(context), new File(context.getExternalCacheDir(), "attachments/" + task.getId()), task, ignoreCache);
	}
}
