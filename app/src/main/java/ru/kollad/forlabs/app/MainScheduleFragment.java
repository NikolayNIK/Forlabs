package ru.kollad.forlabs.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Semesters;
import ru.kollad.forlabs.model.StudentInfo;
import ru.kollad.forlabs.util.Keys;
import ru.kollad.forlabs.util.SerializableUtil;
import ru.kollad.forlabs.viewmodel.MainScheduleFragmentViewModel;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class MainScheduleFragment extends MainFragment implements Observer<JSONArray> {

	private static final String KEY_STUDENT_INFO = "studentInfo";

	private MainScheduleFragmentViewModel model;
	private StudentInfo studentInfo;

	private View coordinator, cardSelector;
	private ViewGroup containerSchedule;
	private Spinner spinnerCourse, spinnerStream, spinnerWeek;

	static MainScheduleFragment newInstance(StudentInfo studentInfo) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_STUDENT_INFO, studentInfo);

		MainScheduleFragment fragment = new MainScheduleFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = ViewModelProviders.of(this).get(MainScheduleFragmentViewModel.class);

		assert getArguments() != null;
		studentInfo = (StudentInfo) getArguments().getSerializable(KEY_STUDENT_INFO);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_schedule, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		coordinator = view.findViewById(R.id.coordinator);
		containerSchedule = view.findViewById(R.id.container_schedule);

		model.getIndex().observe(this, this);
		onChanged(model.getIndex().getValue());

		model.getSchedule().observe(this, new Observer<JSONObject>() {
			@Override
			public void onChanged(JSONObject jsonObject) {
				clearSchedule();
				if (jsonObject != null && getActivity() != null) {
					Calendar calendar = Calendar.getInstance();

					int week = spinnerWeek.getSelectedItemPosition();
					int currentWeek = calendar.get(Calendar.WEEK_OF_MONTH) % 2;
					int currentDay = currentWeek == week ? calendar.get(Calendar.DAY_OF_WEEK) - 1 : -1;
					int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
					int currentMinute = calendar.get(Calendar.MINUTE);
					if (currentDay == 0) currentDay = 7;

					for (int day = 1; day <= 7; day++) {
						JSONObject dayJson = jsonObject.optJSONObject("day" + (7 * week + day));
						if (dayJson != null) {
							View viewDay = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_schedule_day, containerSchedule, false);

							ViewGroup groupItems = viewDay.findViewById(R.id.layout_schedule_items);
							TextView textTitle = groupItems.findViewById(R.id.text_title);
							textTitle.setText(getResources().getStringArray(R.array.days)[day - 1]);
							if (day == currentDay)
								textTitle.setTypeface(textTitle.getTypeface(), Typeface.BOLD);

							Iterator<String> keys = dayJson.keys();
							while (keys.hasNext()) {
								String key = keys.next();
								JSONObject itemJson = dayJson.optJSONObject(key);
								if (itemJson != null) {
									String name = itemJson.optString("study");

									View viewItem = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_schedule_day_item, groupItems, false);
									viewItem.setOnClickListener(new OnStudyClickListener(name));
									((TextView) viewItem.findViewById(R.id.text_time_start)).setText(key.substring(0, 5));
									((TextView) viewItem.findViewById(R.id.text_time_end)).setText(key.substring(6));
									((TextView) viewItem.findViewById(R.id.text_name)).setText(name);
									((TextView) viewItem.findViewById(R.id.text_room)).setText(itemJson.optString("room"));
									((TextView) viewItem.findViewById(R.id.text_lecturer)).setText(formatTeacher(itemJson.optString("lecturer")));

									if (day == currentDay &&
											Integer.valueOf(key.substring(0, 2)) <= currentHour &&
											Integer.valueOf(key.substring(3, 5)) <= currentMinute &&
											Integer.valueOf(key.substring(6, 8)) >= currentHour &&
											Integer.valueOf(key.substring(9, 11)) >= currentMinute)
										viewItem.setBackgroundResource(R.color.accent12);

									groupItems.addView(viewItem);
								}
							}

							containerSchedule.addView(viewDay);
						}
					}
				}
			}
		});
	}

	@Override
	public void onChanged(final JSONArray indexArray) {
		if (cardSelector != null) containerSchedule.removeView(cardSelector);
		if (indexArray == null) {
			model.fetchIndex(getContext());
		} else if (indexArray.length() == 0) {
			Snackbar.make(coordinator, "Empty", Snackbar.LENGTH_INDEFINITE).show();
		} else if (getActivity() != null) {
			cardSelector = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_schedule_selector, containerSchedule, false);

			String[] courses = new String[indexArray.length()];
			if (model.getCourse() == -1) {
				String item;
				String crap = Integer.toString(studentInfo.getCurrentCourse());
				for (int i = 0; i < courses.length; i++) {
					try {
						item = indexArray.getJSONObject(i).getString("course");
					} catch (JSONException e) {
						item = e.toString();
					}

					courses[i] = item;
					if (item.startsWith(crap)) {
						model.setCourse(i);
					}
				}
			} else {
				for (int i = 0; i < courses.length; i++) {
					try {
						courses[i] = indexArray.getJSONObject(i).getString("course");
					} catch (JSONException e) {
						courses[i] = e.toString();
					}
				}
			}

			spinnerCourse = cardSelector.findViewById(R.id.spinner_course);

			final ArrayAdapter<String> adapterStreams = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

			spinnerStream = cardSelector.findViewById(R.id.spinner_stream);
			spinnerStream.setAdapter(adapterStreams);
			spinnerStream.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if (position == 0) {
						model.getSchedule().setValue(null);
					} else {
						model.setStream(position);
						try {
							model.fetchSchedule(getContext(), indexArray.getJSONObject(spinnerCourse.getSelectedItemPosition()).getJSONArray("streams").getJSONObject(position - 1).getString("link"));
						} catch (JSONException e) {
							Log.e("Forlabs", "So kostil", e);
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			spinnerCourse.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, courses));
			spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					try {
						JSONArray jsonArray = indexArray.getJSONObject(position).getJSONArray("streams");

						adapterStreams.clear();
						adapterStreams.add(getString(R.string.spinner_schedule_choose));
						if (model.getStream() == -1 || model.getCourse() == position) {
							String item;
							String crap = studentInfo.getGroupName();
							for (int i = 0; i < jsonArray.length(); i++) {
								try {
									adapterStreams.add(item = jsonArray.getJSONObject(i).getString("code"));
								} catch (JSONException e) {
									adapterStreams.add(item = e.toString());
								}

								if (crap.contains(item)) {
									model.setStream(i + 1);
								}
							}
						} else {
							for (int i = 0; i < jsonArray.length(); i++) {
								try {
									adapterStreams.add(jsonArray.getJSONObject(i).getString("code"));
								} catch (JSONException e) {
									adapterStreams.add(e.toString());
								}
							}

							model.setStream(0);
						}

						spinnerStream.setSelection(model.getStream());
						model.setCourse(position);
					} catch (JSONException e) {
						Snackbar.make(coordinator, e.toString(), Snackbar.LENGTH_INDEFINITE).show();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			spinnerCourse.setSelection(model.getCourse());

			spinnerWeek = cardSelector.findViewById(R.id.spinner_week);
			spinnerWeek.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.weeks)));
			spinnerWeek.setSelection(model.getWeek() == -1 ? Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) % 2 : model.getWeek());
			spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if (model.getWeek() != position) {
						model.setWeek(position);
						model.getSchedule().setValue(model.getSchedule().getValue());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			containerSchedule.addView(cardSelector);
		}
	}

	private String formatTeacher(String teacher) {
		String[] array = teacher.split(" ");
		StringBuilder sb = new StringBuilder(array[0]);
		sb.append(' ');
		for (int i = 1; i < array.length; i++) {
			sb.append(array[i].charAt(0));
			sb.append(". ");
		}

		return sb.toString();
	}

	private void clearSchedule() {
		containerSchedule.removeViews(1, containerSchedule.getChildCount() - 1);
	}

	private class OnStudyClickListener implements View.OnClickListener {

		private final String name;

		private OnStudyClickListener(String name) {
			this.name = name;
		}

		@Override
		public void onClick(View v) {
			try { // Hope for the best
				startActivity(new Intent(getContext(), StudyActivity.class).putExtra(StudyActivity.EXTRA_STUDY, ((Semesters) SerializableUtil.read(Keys.getStudiesFile(getContext()))).findStudy(name)));
			} catch (Exception e) {
				Log.w("Forlabs", "Unable to open StudyActivity", e);
			}
		}
	}
}
