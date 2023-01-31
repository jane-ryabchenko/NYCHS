package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.SatResults;
import com.jriabchenko.nychs.network.SchoolDetails;

import java.util.List;

/** View model for the detailed view. */
public class SchoolDetailsViewModel extends ViewModel {
  private OpenDataService openDataService;

  public void setService(OpenDataService openDataService) {
    this.openDataService = openDataService;
  }

  public LiveData<SchoolDetails> loadSchoolDetails(String dbn, FailureHandler failureHandler) {
    MutableLiveData<SchoolDetails> schoolDetails = new MutableLiveData<>();
    openDataService.getSchoolDetails(dbn, schoolDetails::postValue, failureHandler);
    return schoolDetails;
  }

  public LiveData<List<SatResults>> loadSatResults(String dbn, FailureHandler failureHandler) {
    MutableLiveData<List<SatResults>> satResults = new MutableLiveData<>();
    openDataService.getSatResults(dbn, satResults::postValue, failureHandler);
    return satResults;
  }
}
