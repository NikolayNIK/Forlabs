package ru.kollad.forlabs.app;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by NikolayNIK on 18.03.2019.
 */
public abstract class TaskFragment extends Fragment {

	MutableLiveData<Integer> getCounter() {
		return ((TaskActivity) requireActivity()).getCounter();
	}

	@Override
	public void onAttach(Context context) {
		if (!(context instanceof TaskActivity))
			throw new IllegalStateException("TaskFragment may only be attached to TaskActivity");

		super.onAttach(context);
	}

	abstract void refresh();
}
