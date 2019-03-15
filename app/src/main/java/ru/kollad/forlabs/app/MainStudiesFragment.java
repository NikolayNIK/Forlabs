package ru.kollad.forlabs.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Semesters;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.util.RefreshUtil;
import ru.kollad.forlabs.viewmodel.MainStudiesFragmentViewModel;
import ru.kollad.forlabs.widget.SemesterAdapter;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class MainStudiesFragment extends MainFragment implements Observer<Semesters>, SemesterAdapter.OnItemClickListener {

	private MainStudiesFragmentViewModel model;

	private SemesterAdapter adapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = ViewModelProviders.of(this).get(MainStudiesFragmentViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_studies, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView recycler = view.findViewById(R.id.recycler);
		recycler.setLayoutManager(new GridLayoutManager(getContext(), getResources().getConfiguration().screenWidthDp / 600 + 1));
		recycler.setAdapter(adapter = new SemesterAdapter(getContext()));
		adapter.setOnItemClickListener(this);

		model.getStudies().observe(this, this);
		if (model.getStudies().getValue() == null) model.fetchStudies(getContext(), false);
		else onChanged(model.getStudies().getValue());

		View buttonRefresh = view.findViewById(R.id.button_refresh);
		RefreshUtil.observeRefresh(buttonRefresh, this, model.getRefreshing());
		buttonRefresh.setOnClickListener((v) -> model.fetchStudies(requireContext(), true));
	}

	@Override
	public void onChanged(Semesters semesters) {
		Menu menu = getToolbar().getMenu();
		menu.clear();

		if (semesters == null) return;

		for (int i = 0; i < semesters.size(); i++) {
			String name = semesters.get(i).getName();
			if (name == null) name = getString(R.string.item_semester_current);
			menu.add(-1, i, Menu.NONE, name);
		}

		menu.setGroupCheckable(-1, true, true);
		menu.findItem(model.getSelectedSemester()).setChecked(true);
		adapter.setSemester(semesters.get(model.getSelectedSemester()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (model.getStudies().getValue() == null ||
				item.getItemId() < 0 || item.getItemId() >= model.getStudies().getValue().size())
			return false;

		item.setChecked(true);
		model.setSelectedSemester(item.getItemId());
		adapter.setSemester(model.getStudies().getValue().get(item.getItemId()));
		return true;
	}

	@Override
	public void onClickItemStudy(SemesterAdapter adapter, Study item) {
		startActivity(new Intent(getContext(), StudyActivity.class)
				.putExtra(StudyActivity.EXTRA_STUDY, item));
	}
}
