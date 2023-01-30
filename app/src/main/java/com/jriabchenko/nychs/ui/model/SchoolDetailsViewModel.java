package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.NYCHSApiService;
import com.jriabchenko.nychs.network.SATResults;
import com.jriabchenko.nychs.network.SchoolDetails;

import java.util.List;

public class SchoolDetailsViewModel extends ViewModel {
  private final NYCHSApiService api = new NYCHSApiService();

  public LiveData<SchoolDetails> loadSchoolDetails(String dbn, FailureHandler failureHandler) {
    MutableLiveData<SchoolDetails> schoolDetails = new MutableLiveData<>();
    api.getSchoolDetails(dbn, schoolDetails::postValue, failureHandler);
    return schoolDetails;
  }

  public LiveData<List<SATResults>> loadSATResults(String dbn, FailureHandler failureHandler) {
    MutableLiveData<List<SATResults>> satResults = new MutableLiveData<>();
    api.getSATResults(dbn, satResults::postValue, failureHandler);
    return satResults;
  }
}
