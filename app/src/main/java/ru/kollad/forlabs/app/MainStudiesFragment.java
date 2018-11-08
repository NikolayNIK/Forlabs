package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.kollad.forlabs.R;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class MainStudiesFragment extends MainFragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_studies, container, false);
	}
}
