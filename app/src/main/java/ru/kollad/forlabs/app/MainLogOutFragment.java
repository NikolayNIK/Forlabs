package ru.kollad.forlabs.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.tasks.LogOutTask;
import ru.kollad.forlabs.util.Keys;

/**
 * Created by NikolayNIK on 09.11.2018.
 */
public class MainLogOutFragment extends DialogFragment implements LogOutTask.OnPostExecuteListener {

	private LogOutTask task;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (task == null) {
			task = new LogOutTask(this);
			task.execute(Keys.getCookiesFile(context));
		}
	}

	@SuppressLint("InflateParams")
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		assert getActivity() != null;

		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		adb.setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_log_out, null));
		adb.setCancelable(false);
		return adb.create();
	}

	@Override
	public void onPostExecute(LogOutTask task, boolean success) {
		if (success) {
			Activity activity = getActivity();
			if (activity != null) {
				activity.startActivity(new Intent(activity, AuthActivity.class));
				activity.finish();
			}
		} else {
			dismissAllowingStateLoss();
		}
	}
}
