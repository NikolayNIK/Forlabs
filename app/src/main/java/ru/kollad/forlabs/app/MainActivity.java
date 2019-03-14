package ru.kollad.forlabs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.util.Keys;

public class MainActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener {

	static final String EXTRA_STUDENT_INFO = "studentInfo";
	static final String EXTRA_START_STATE = "state";

	private static final Uri URI_ABOUT = Uri.parse("https://kollad.ru/products/forlabs/");

	@Nullable
	private DrawerLayout drawerLayout;
	private StudentInfo studentInfo;
	private int state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawerLayout = findViewById(R.id.drawer_layout);

		studentInfo = (StudentInfo) getIntent().getSerializableExtra(EXTRA_STUDENT_INFO);

		NavigationView navigation = findViewById(R.id.navigation);
		navigation.setNavigationItemSelectedListener(this);

		View header = navigation.getHeaderView(0);
		Glide.with(this).load(studentInfo.getStudentPhotoUrl())
				.apply(new RequestOptions().circleCrop().dontAnimate())
				.into((ImageView) header.findViewById(R.id.image_avatar));
		((TextView) header.findViewById(R.id.text_name)).setText(studentInfo.getStudentName());

		if (savedInstanceState == null) {
			state = getIntent().getIntExtra(EXTRA_START_STATE, PreferenceManager.getDefaultSharedPreferences(this).getInt(Keys.DEFAULT_SECTION, 0));
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			switch (state) {
				case 0:
					navigation.getMenu().findItem(R.id.item_dashboard).setChecked(true);
					transaction.replace(R.id.container, MainDashboardFragment.newInstance(studentInfo));
					break;
				case 1:
					navigation.getMenu().findItem(R.id.item_schedule).setChecked(true);
					transaction.replace(R.id.container, MainScheduleFragment.newInstance(studentInfo));
					break;
				case 2:
					navigation.getMenu().findItem(R.id.item_studies).setChecked(true);
					transaction.replace(R.id.container, new MainStudiesFragment());
					break;
			}
			transaction.commit();
		} else {
			state = savedInstanceState.getInt("state");
			switch (state) {
				case 0:
					navigation.getMenu().findItem(R.id.item_dashboard).setChecked(true);
					break;
				case 1:
					navigation.getMenu().findItem(R.id.item_schedule).setChecked(true);
					break;
				case 2:
					navigation.getMenu().findItem(R.id.item_studies).setChecked(true);
					break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				openDrawer();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
			return;
		}

		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("state", state);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.item_dashboard:
				state = 0;
				replace(MainDashboardFragment.newInstance(studentInfo));
				closeDrawer();
				return true;
			case R.id.item_schedule:
				state = 1;
				replace(MainScheduleFragment.newInstance(studentInfo));
				closeDrawer();
				return true;
			case R.id.item_studies:
				state = 2;
				replace(new MainStudiesFragment());
				closeDrawer();
				return true;
			case R.id.item_other_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				closeDrawer();
				return true;
			case R.id.item_other_about:
				startActivity(new Intent(Intent.ACTION_VIEW, URI_ABOUT));
				closeDrawer();
				return true;
			case R.id.item_other_report:
				startActivity(new Intent(this, ReportActivity.class));
				closeDrawer();
				return true;
			case R.id.item_other_logout:
				closeDrawer();
				getSupportFragmentManager().beginTransaction()
						.add(new MainLogOutFragment(), null)
						.commit();
				return true;
			default:
				return false;
		}
	}

	void openDrawer() {
		if (drawerLayout != null)
			drawerLayout.openDrawer(GravityCompat.START);
	}

	private void closeDrawer() {
		if (drawerLayout != null)
			drawerLayout.closeDrawers();
	}

	private void replace(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.transition_enter, R.anim.transition_exit)
				.replace(R.id.container, fragment)
				.commit();
	}
}
