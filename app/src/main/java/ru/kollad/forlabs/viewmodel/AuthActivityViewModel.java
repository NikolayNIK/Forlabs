package ru.kollad.forlabs.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class AuthActivityViewModel extends ViewModel {

	private final MutableLiveData<Boolean> tokenStatus = new MutableLiveData<>();

	public LiveData<Boolean> getTokenStatus() {
		return tokenStatus;
	}

	public void checkTokenStatus() {

	}
}
