package ru.kollad.forlabs.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.tasks.ReportTask;

/**
 * Created by NikolayNIK on 13.11.2018.
 */
public class ReportFragment extends DialogFragment implements ReportTask.OnPostExecuteListener {

	private static final String KEY_TEXT = "text";

	private ReportTask task;

	static ReportFragment newInstance(String text) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_TEXT, text);

		ReportFragment fragment = new ReportFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.setRetainInstance(true);
		super.onCreate(savedInstanceState);
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
	public void onAttach(Context context) {
		super.onAttach(context);

		if (task == null) {
			assert getArguments() != null;

			task = new ReportTask(this);
			task.execute(getArguments().getString(KEY_TEXT));
		}
	}

	@Override
	public void onPostExecute(ReportTask task, @Nullable Exception exception) {
		if (exception == null) {
			if (getActivity() != null) {
				Toast.makeText(getActivity(), R.string.toast_report_success, Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
		} else {
			if (getContext() != null)
				Toast.makeText(getContext(), exception.toString(), Toast.LENGTH_LONG).show();
			dismiss();
		}
	}
}
