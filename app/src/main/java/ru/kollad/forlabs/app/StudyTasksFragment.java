package ru.kollad.forlabs.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.model.Task;

/**
 * Created by NikolayNIK on 16.11.2018.
 */
public class StudyTasksFragment extends StudyFragment {

	private int getStatusResource(Task task) {
		Task.Assignment assignment = task.getAssignment();
		switch (task.getStatus()) {
			case 1:
				if (assignment != null) {
					switch (assignment.getStatus()) {
						case 2:
							return R.string.text_study_tasks_status_sent;
						case 3:
							return R.string.text_study_tasks_status_accepted;
						case 6:
							return R.string.text_study_tasks_status_teacher_answered;
						case 7:
							return R.string.text_study_tasks_status_teacher_questions;
					}
				}

				return R.string.text_study_tasks_status_queued;
			case 2:
				if (assignment != null) {
					switch (assignment.getStatus()) {
						case 2:
							return R.string.text_study_tasks_status_sent;
						case 3:
							return R.string.text_study_tasks_status_accepted;
						case 6:
							return R.string.text_study_tasks_status_teacher_answered;
						case 7:
							return R.string.text_study_tasks_status_teacher_questions;
					}
				}

				return R.string.text_study_tasks_status_current;
			case 3:
				if (assignment != null) {
					switch (assignment.getStatus()) {
						case 1:
							return R.string.text_study_tasks_status_debt;
						case 2:
							return R.string.text_study_tasks_status_sent;
						case 3:
							return R.string.text_study_tasks_status_done;
						case 6:
							return R.string.text_study_tasks_status_teacher_answered;
						case 7:
							return R.string.text_study_tasks_status_teacher_questions;
					}
				}

				return R.string.text_study_tasks_status_done;
		}

		Log.w("Forlabs", "Unknown task status: " + task.getStatus());
		return 0;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_tasks, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {
		View scrollTasks = view.findViewById(R.id.scroll_tasks);
		View textEmpty = view.findViewById(R.id.text_empty);
		if (study == null) {
			scrollTasks.setVisibility(View.GONE);
			textEmpty.setVisibility(View.GONE);
		} else {
			if (study.getTasks().isEmpty()) {
				scrollTasks.setVisibility(View.GONE);
				textEmpty.setVisibility(View.VISIBLE);
			} else {
				scrollTasks.setVisibility(View.VISIBLE);
				textEmpty.setVisibility(View.GONE);

				ViewGroup layoutTasks = scrollTasks.findViewById(R.id.layout_tasks);
				for (Task task : study.getTasks()) {
					View viewTask = getLayoutInflater().inflate(R.layout.fragment_study_tasks_item, layoutTasks, false);
					viewTask.setOnClickListener(new OnTaskClickListener(task));
					((TextView) viewTask.findViewById(R.id.text_name)).setText(task.getName());
					((TextView) viewTask.findViewById(R.id.text_status)).setText(getStatusResource(task));
					((TextView) viewTask.findViewById(R.id.text_score)).setText(task.getAssignment() == null || task.getAssignment().getAssessment() == null ?
							getString(R.string.text_study_tasks_cost, task.getCost()) :
							getString(R.string.text_study_tasks_score, task.getAssignment().getAssessment().getCredits(), task.getCost()));

					layoutTasks.addView(viewTask);
				}
			}
		}
	}

	private class OnTaskClickListener implements View.OnClickListener {

		private final Task task;

		private OnTaskClickListener(Task task) {
			this.task = task;
		}

		@Override
		public void onClick(View v) {
			try {
				startActivity(new Intent(getContext(), TaskActivity.class)
						.putExtra(TaskActivity.EXTRA_TASK, task));
			} catch (Exception e) {
				Log.w("Forlabs", "Unable to start TaskActivity", e);
			}
		}
	}
}
