package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Studies;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.viewmodel.MainStudiesFragmentViewModel;
import ru.kollad.forlabs.widget.StudiesAdapter;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class MainStudiesFragment extends MainFragment implements Observer<Studies>, StudiesAdapter.OnItemClickListener {

	private MainStudiesFragmentViewModel model;

	private StudiesAdapter adapter;

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
		recycler.setAdapter(adapter = new StudiesAdapter(getContext()));
		adapter.setOnItemClickListener(this);

		model.getStudies().observe(this, this);
		if (model.getStudies().getValue() == null) model.fetchStudies(getContext());
		else onChanged(model.getStudies().getValue());
	}

	@Override
	public void onChanged(Studies studies) {
		adapter.setStudies(studies);
	}

	@Override
	public void onClickItemStudy(StudiesAdapter adapter, Study item) {
		// TODO
	}
}
