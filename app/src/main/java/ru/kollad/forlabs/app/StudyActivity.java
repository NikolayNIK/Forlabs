package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;
import ru.kollad.forlabs.viewmodel.StudyActivityViewModel;

public class StudyActivity extends AppCompatActivity implements Observer<Study> {

	static final String EXTRA_STUDY = "study";

	private PagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_daynight_24dp);

		setupTabs();

		StudyActivityViewModel model = ViewModelProviders.of(this).get(StudyActivityViewModel.class);
		model.getStudy().observe(this, this);
		Study study;
		if (model.getStudy().getValue() == null) {
			study = (Study) getIntent().getSerializableExtra(EXTRA_STUDY);
			model.fetchStudy(this, study);
		} else {
			study = model.getStudy().getValue();
			onChanged(study);
		}

		setTitle(study.getName());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onChanged(Study study) {
		for (StudyFragment fragment : adapter.fragments)
			fragment.setStudy(study);
	}

	private void setupTabs() {
		adapter = new PagerAdapter(getSupportFragmentManager());
		ViewPager viewPager = findViewById(R.id.container);
		viewPager.setAdapter(adapter);

		TabLayout tabLayout = findViewById(R.id.tabs);

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {

		private final StudyFragment[] fragments = new StudyFragment[]{
				new StudyOverviewFragment(),
				new StudyTasksFragment(),
				new StudyFragment(),
				new StudyAttendanceFragment(),
				new StudyFragment()
		};

		private PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public StudyFragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public int getCount() {
			return 5;
		}
	}
}
