package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.School;

import java.util.ArrayList;
import java.util.List;

/** View model for the list view. */
public class SchoolListViewModel extends ViewModel {
  private final MutableLiveData<List<School>> schools = new MutableLiveData<>(new ArrayList<>());

  private OpenDataService openDataService;

  public void setService(OpenDataService openDataService) {
    this.openDataService = openDataService;
  }

  public LiveData<List<School>> loadMoreSchools(int pageSize, FailureHandler failureHandler) {
    List<School> current = schools.getValue();
    openDataService.getSchoolList(
        pageSize,
        current.size(),
        results -> {
          List<School> updated = new ArrayList<>(current);
          updated.addAll(results);
          schools.postValue(updated);
        },
        failureHandler);
    return schools;
  }
}
