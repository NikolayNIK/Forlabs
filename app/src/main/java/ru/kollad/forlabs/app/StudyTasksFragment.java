package ru.kollad.forlabs.app;

import android.os.Bundle;
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

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_study_tasks, container, false);
	}

	@Override
	protected void onStudyChanged(@NonNull View view, @Nullable Study study) {
		View cardTasks = view.findViewById(R.id.card_tasks);
		View textEmpty = view.findViewById(R.id.text_empty);
		if (study == null) {
			cardTasks.setVisibility(View.GONE);
			textEmpty.setVisibility(View.GONE);
		} else {
			if (study.getTasks().isEmpty()) {
				cardTasks.setVisibility(View.GONE);
				textEmpty.setVisibility(View.VISIBLE);
			} else {
				cardTasks.setVisibility(View.VISIBLE);
				textEmpty.setVisibility(View.GONE);

				ViewGroup layoutTasks = cardTasks.findViewById(R.id.layout_tasks);
				for (Task task : study.getTasks()) {
					View viewTask = getLayoutInflater().inflate(R.layout.fragment_study_tasks_item, layoutTasks, false);
					((TextView) viewTask.findViewById(R.id.text_name)).setText(task.getName());
					((TextView) viewTask.findViewById(R.id.text_status)).setText(getResources().getStringArray(R.array.task_statuses)[task.getAssignment() == null ? task.getStatus() : task.getAssignment().getStatus()]); // TODO
					((TextView) viewTask.findViewById(R.id.text_score)).setText(task.getAssignment() == null || task.getAssignment().getAssessment() == null ?
							getString(R.string.text_study_tasks_cost, task.getCost()) :
							getString(R.string.text_study_tasks_score, task.getAssignment().getAssessment().getCredits(), task.getCost()));

					layoutTasks.addView(viewTask);
				}
			}
		}
	}
}
