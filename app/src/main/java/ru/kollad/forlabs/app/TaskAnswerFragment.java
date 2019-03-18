package ru.kollad.forlabs.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.util.DocumentFileUtil;
import ru.kollad.forlabs.viewmodel.TaskAnswerFragmentViewModel;
import ru.kollad.forlabs.widget.MessageAdapter;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class TaskAnswerFragment extends Fragment implements Observer<List<Message>>, TextWatcher {

	private static final String KEY_TASK = "task";
	private static final String KEY_ATTACHED_URIS = "attached_uris";

	private static final int REQUEST_CHOOSE_ATTACHMENT = 69;

	private ArrayList<Uri> attachedUris;
	private ViewGroup layoutAttachments;
	private MessageAdapter adapter;
	private View buttonAttach, buttonSend, textEmpty;
	private EditText editMessage;

	static TaskAnswerFragment newInstance(Task task) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_TASK, task);

		TaskAnswerFragment fragment = new TaskAnswerFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_task_answer, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		assert getArguments() != null;

		final Task task = (Task) getArguments().getSerializable(KEY_TASK);
		assert task != null;

		layoutAttachments = view.findViewById(R.id.layout_attachments);
		buttonAttach = view.findViewById(R.id.button_attach);
		buttonSend = view.findViewById(R.id.button_send);
		editMessage = view.findViewById(R.id.edit_message);
		textEmpty = view.findViewById(R.id.text_empty);

		adapter = new MessageAdapter(getContext());

		RecyclerView recycler = view.findViewById(R.id.recycler);
		recycler.setLayoutManager(new LinearLayoutManager(getContext()));
		recycler.setAdapter(adapter);

		final TaskAnswerFragmentViewModel model = ViewModelProviders.of(this).get(TaskAnswerFragmentViewModel.class);
		model.getMessages().observe(this, this);
		if (model.getMessages().getValue() == null) {
			model.fetchMessages(getContext(), task, false);
		}

		if (savedInstanceState == null) attachedUris = new ArrayList<>();
		else attachedUris = savedInstanceState.getParcelableArrayList(KEY_ATTACHED_URIS);

		assert attachedUris != null;
		for (Uri uri : attachedUris)
			inflateAttachment(uri);

		if (attachedUris.size() >= 4)
			buttonAttach.setVisibility(View.GONE);

		buttonAttach.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*")
								.addCategory(Intent.CATEGORY_OPENABLE),
						REQUEST_CHOOSE_ATTACHMENT);
			}
		});

		checkSendButton();
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				model.sendMessage(getContext(), task, editMessage.getText().toString(), attachedUris);
				editMessage.setText(null);
				layoutAttachments.removeAllViews();
				attachedUris.clear();
			}
		});

		editMessage.addTextChangedListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (attachedUris.size() < 4 && requestCode == REQUEST_CHOOSE_ATTACHMENT && resultCode == Activity.RESULT_OK &&
				data != null && data.getData() != null) { // Paranoia
			attachedUris.add(data.getData());
			inflateAttachment(data.getData());
			buttonSend.setEnabled(true);

			if (attachedUris.size() >= 4)
				buttonAttach.setVisibility(View.GONE);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelableArrayList(KEY_ATTACHED_URIS, attachedUris);
	}

	@Override
	public void onChanged(List<Message> messages) {
		adapter.setMessages(messages);
		textEmpty.setVisibility(messages != null && messages.isEmpty() ? View.VISIBLE : View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		checkSendButton();
	}

	private void inflateAttachment(final Uri uri) {
		assert getContext() != null;
		DocumentFile file = DocumentFileUtil.fromUri(getContext(), uri);
		if (file != null) {
			String name = file.getName();
			double length = file.length();

			final View view = getLayoutInflater().inflate(R.layout.item_attachment, layoutAttachments, false);
			((ImageView) view.findViewById(R.id.image_thumbnail)).setImageResource(R.drawable.ic_attachment_daynight_48dp);
			((TextView) view.findViewById(R.id.text_name)).setText(name);

			int i;
			String[] units = getResources().getStringArray(R.array.file_size_units);
			for (i = 0; i < units.length; i++) {
				double tmp = length / 1024d;
				if (tmp < 1) break;
				length = tmp;
			}

			((TextView) view.findViewById(R.id.text_size)).setText(getString(R.string.text_task_attachment_size, length, units[i]));

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					attachedUris.remove(uri);
					layoutAttachments.removeView(view);
					buttonAttach.setVisibility(View.VISIBLE);
					checkSendButton();
				}
			});

			layoutAttachments.addView(view);
		}
	}

	private void checkSendButton() {
		buttonSend.setEnabled(!attachedUris.isEmpty() || !TextUtils.isEmpty(editMessage.getText()));
	}
}
