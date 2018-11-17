package ru.kollad.forlabs.app;

import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Task;

/**
 * Created by NikolayNIK on 17.11.2018.
 */
public class TaskDescriptionFragment extends Fragment {

	private static final String KEY_TASK = "task";

	private static final String CONTENT_PREFIX = "<style>body{color:#%s;background-color:#%s;}</style><body>";
	private static final String CONTENT_SUFFIX = "</body>";

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
		Task task = (Task) getArguments().getSerializable(KEY_TASK);

		assert getContext() != null;
		assert task != null;

		TypedArray ta = getContext().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
		int color = ContextCompat.getColor(getContext(), ta.getResourceId(0, R.color.accent));
		ta.recycle();

		((WebView) view.findViewById(R.id.web)).loadData(
				Uri.encode(String.format(CONTENT_PREFIX,
						Integer.toHexString(color).substring(2),
						Integer.toHexString(ContextCompat.getColor(getContext(), R.color.background)).substring(2))
						+ task.getContent() + CONTENT_SUFFIX), "text/html", null);
	}
}
