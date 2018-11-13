package ru.kollad.forlabs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.StudentInfo;

public class MainActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener {

	static final String EXTRA_STUDENT_INFO = "studentInfo";
	static final String EXTRA_START_STATE = "state";

	private static final Uri URI_ABOUT = Uri.parse("https://kollad.ru");
	private static final Uri URI_REPORT = Uri.parse("https://github.com/NikolayNIK/Rettel/issues/new");

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
			state = getIntent().getIntExtra(EXTRA_START_STATE, 0);
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
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_schedule:
				state = 1;
				replace(MainScheduleFragment.newInstance(studentInfo));
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_studies:
				state = 2;
				replace(new MainStudiesFragment());
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_other_settings:
				// TODO Account settings
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_other_about:
				startActivity(new Intent(Intent.ACTION_VIEW, URI_ABOUT));
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_other_report:
				startActivity(new Intent(this, ReportActivity.class));
				drawerLayout.closeDrawers();
				return true;
			case R.id.item_other_logout:
				drawerLayout.closeDrawers();
				getSupportFragmentManager().beginTransaction()
						.add(new MainLogOutFragment(), null)
						.commit();
				return true;
			default:
				return false;
		}
	}

	void openDrawer() {
		drawerLayout.openDrawer(GravityCompat.START);
	}

	private void replace(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.transition_enter, R.anim.transition_exit)
				.replace(R.id.container, fragment)
				.commit();
	}
}
