package ru.kollad.forlabs.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by NikolayNIK on 18.03.2019.
 */
public class TaskActivityViewModel extends ViewModel {

	public final MutableLiveData<Integer> refreshCounter = new MutableLiveData<>();
}
