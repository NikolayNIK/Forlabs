package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Task;
import ru.kollad.forlabs.viewmodel.TaskActivityViewModel;

public class TaskActivity extends AppCompatActivity {

	static final String EXTRA_TASK = "task";

	private TaskActivityViewModel model;
	private View buttonRefresh;
	private Task task;

	MutableLiveData<Integer> getCounter() {
		return model.refreshCounter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		model = ViewModelProviders.of(this).get(TaskActivityViewModel.class);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		buttonRefresh = findViewById(R.id.button_refresh);

		setSupportActionBar(findViewById(R.id.toolbar));
		assert getSupportActionBar() != null;

		task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
		getSupportActionBar().setTitle(task.getName());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_daynight_24dp);

		setupTabs();

		model.refreshCounter.observe(this, (value) -> {
			if (buttonRefresh.isEnabled() && value != null && value == 1)
				animateRefresh();
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupTabs() {
		PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
		ViewPager viewPager = findViewById(R.id.container);
		viewPager.setAdapter(adapter);

		TabLayout tabLayout = findViewById(R.id.tabs);

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

		buttonRefresh.setOnClickListener((view) -> {
			for (TaskFragment fragment : adapter.fragments)
				fragment.refresh();
		});
	}

	private void animateRefresh() {
		buttonRefresh.setEnabled(false);
		buttonRefresh.setRotation(buttonRefresh.getRotation() - 360);
		buttonRefresh.animate()
				.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
				.setInterpolator(new LinearInterpolator()).rotation(0).withEndAction(() -> {
			if (model.refreshCounter.getValue() != null && model.refreshCounter.getValue() > 0)
				animateRefresh();
			else
				buttonRefresh.setEnabled(true);
		}).start();
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {

		private final TaskFragment[] fragments = new TaskFragment[]{
				TaskDescriptionFragment.newInstance(task),
				TaskAnswerFragment.newInstance(task),
		};

		private PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

}
