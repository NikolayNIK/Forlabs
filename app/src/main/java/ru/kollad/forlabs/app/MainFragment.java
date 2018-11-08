package ru.kollad.forlabs.app;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import ru.kollad.forlabs.R;

/**
 * Abstract class that may only be attached to {@link MainActivity} and can use all of it's methods.
 * Also implements options menu if fragment's content view contains
 * {@link androidx.appcompat.widget.Toolbar} with id @+id/toolbar and implements opening drawer on
 * android.R.id.home menuItem selection.
 * Created by NikolayNIK on 08.11.2018.
 */
abstract class MainFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

	@Nullable
	protected MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Context context) {
		if (!(context instanceof MainActivity))
			throw new IllegalStateException("MainFragment may only be attached to MainActivity (in the end that what it is for)");
		super.onAttach(context);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Toolbar toolbar = view.findViewById(R.id.toolbar);
		if (toolbar != null && getActivity() != null) {
			onCreateOptionsMenu(toolbar.getMenu(), getActivity().getMenuInflater());
			toolbar.setOnMenuItemClickListener(this);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity activity = getMainActivity();
					if (activity != null) activity.openDrawer();
				}
			});
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onOptionsItemSelected(item);
	}
}
