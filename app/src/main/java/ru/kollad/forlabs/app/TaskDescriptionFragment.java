package ru.kollad.forlabs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.viewmodel.TaskDescriptionFragmentViewModel;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class TaskDescriptionFragment extends Fragment implements Observer<List<Attachment>> {

	private static final String KEY_TASK = "task";

	private static final String CONTENT = "<html><head><style>body{color:#%s;background-color:#%s;}</style></head><body>%s</body></html>";

	private Task task;

	static TaskDescriptionFragment newInstance(Task task) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_TASK, task);

		TaskDescriptionFragment fragment = new TaskDescriptionFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_task_description, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		assert getArguments() != null;
		task = (Task) getArguments().getSerializable(KEY_TASK);

		assert getContext() != null;
		assert task != null;

		((WebView) view.findViewById(R.id.web)).loadData(
				Uri.encode(String.format(CONTENT,
						Integer.toHexString(ContextCompat.getColor(getContext(), R.color.text)).substring(2),
						Integer.toHexString(ContextCompat.getColor(getContext(), R.color.background)).substring(2),
						task.getContent())), "text/html", null);

		TaskDescriptionFragmentViewModel model = ViewModelProviders.of(this).get(TaskDescriptionFragmentViewModel.class);
		model.getAttachments().observe(this, this);
		if (model.getAttachments().getValue() == null)
			model.fetchAttachments(getContext(), task);
	}

	@Override
	public void onChanged(List<Attachment> attachments) {
		if (getView() != null) {
			if (attachments == null) {
				getView().findViewById(R.id.card_attachments).setVisibility(View.GONE);
				getView().findViewById(R.id.card_title).setVisibility(View.GONE);
			} else {
				if (attachments.isEmpty()) {
					getView().findViewById(R.id.card_attachments).setVisibility(View.GONE);
				} else {
					getView().findViewById(R.id.card_attachments).setVisibility(View.VISIBLE);

					RequestOptions requestOptions = new RequestOptions().circleCrop();
					ViewGroup layoutAttachments = getView().findViewById(R.id.layout_attachments);
					for (Attachment attachment : attachments) {
						View view = getLayoutInflater().inflate(R.layout.item_attachment, layoutAttachments, false);
						view.setOnClickListener(new OnAttachmentClickListener(attachment));
						((TextView) view.findViewById(R.id.text_name)).setText(attachment.getFileName());
						((TextView) view.findViewById(R.id.text_size)).setText(attachment.getHumanFileSize());
						Glide.with(this).load(attachment.getPreviewUrl()).apply(requestOptions)
								.into(((ImageView) view.findViewById(R.id.image_thumbnail)));

						layoutAttachments.addView(view);
					}
				}

				View cardTitle = getView().findViewById(R.id.card_title);
				cardTitle.setVisibility(View.VISIBLE);

				((TextView) cardTitle.findViewById(R.id.text_title))
						.setText(getString(R.string.text_task_title, task.getSort(), task.getName()));
			}
		}
	}

	private class OnAttachmentClickListener implements View.OnClickListener {

		private final Attachment attachment;

		private OnAttachmentClickListener(Attachment attachment) {
			this.attachment = attachment;
		}

		@Override
		public void onClick(View v) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(attachment.getUrl())));
		}
	}
}
