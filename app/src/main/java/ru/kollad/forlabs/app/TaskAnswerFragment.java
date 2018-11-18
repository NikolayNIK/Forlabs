package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Message;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.viewmodel.TaskAnswerFragmentViewModel;
import ru.kollad.forlabs.widget.MessageAdapter;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class TaskAnswerFragment extends Fragment implements Observer<List<Message>> {

	private static final String KEY_TASK = "task";

	private MessageAdapter adapter;

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

		adapter = new MessageAdapter(getContext());

		RecyclerView recycler = view.findViewById(R.id.recycler);
		recycler.setLayoutManager(new LinearLayoutManager(getContext()));
		recycler.setAdapter(adapter);

		TaskAnswerFragmentViewModel model = ViewModelProviders.of(this).get(TaskAnswerFragmentViewModel.class);
		model.getMessages().observe(this, this);
		if (model.getMessages().getValue() == null) {
			assert getArguments() != null;

			Task task = (Task) getArguments().getSerializable(KEY_TASK);
			assert task != null;

			model.fetchMessages(getContext(), task);
		}
	}

	@Override
	public void onChanged(List<Message> messages) {
		adapter.setMessages(messages);
	}
}
