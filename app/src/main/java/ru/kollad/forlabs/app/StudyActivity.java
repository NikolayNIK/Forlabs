package ru.kollad.forlabs.app;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Study;

public class StudyActivity extends AppCompatActivity {

	static final String EXTRA_STUDY = "study";

	private Study study;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_daynight_24dp);

		study = (Study) getIntent().getSerializableExtra(EXTRA_STUDY);
		setTitle(study.getName());

		setupTabs();
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
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {

		private final Fragment[] fragments = new Fragment[]{
				new Fragment(),
				new Fragment(),
				new Fragment(),
				new Fragment(),
				new Fragment()
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
			return 5;
		}
	}
}