package ru.kollad.forlabs.util;

import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

/**
 * Created by NikolayNIK on 15.03.2019.
 */
public class RefreshUtil {

	public static void observeRefresh(@NonNull View view, @NonNull LifecycleOwner owner, @NonNull LiveData<Boolean> liveData) {
		liveData.observe(owner, (value) -> {
			if (liveData.getValue() != null && liveData.getValue())
				animateRefresh(view, liveData);
		});
	}

	private static void animateRefresh(@NonNull View view, @NonNull LiveData<Boolean> liveData) {
		view.setEnabled(false);
		view.setRotation(view.getRotation() - 360);
		view.animate()
				.setDuration(view.getResources().getInteger(android.R.integer.config_longAnimTime))
				.setInterpolator(new LinearInterpolator()).rotation(0).withEndAction(() -> {
			if (liveData.getValue() != null && liveData.getValue())
				animateRefresh(view, liveData);
			else
				view.setEnabled(true);
		}).start();
	}
}
