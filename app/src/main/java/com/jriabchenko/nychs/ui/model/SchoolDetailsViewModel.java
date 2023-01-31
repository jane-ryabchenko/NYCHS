package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.SatResults;
import com.jriabchenko.nychs.network.SchoolDetails;

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

  public LiveData<ImmutableList<SatResults>> loadSatResults(
      String dbn, FailureHandler failureHandler) {
    MutableLiveData<ImmutableList<SatResults>> satResults = new MutableLiveData<>();
    openDataService.getSatResults(dbn, satResults::postValue, failureHandler);
    return satResults;
  }
}
