package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.School;

/** View model for the list view. */
public class SchoolListViewModel extends ViewModel {
  private final MutableLiveData<ImmutableList<School>> schools =
      new MutableLiveData<>(ImmutableList.of());

  private OpenDataService openDataService;

  public void setService(OpenDataService openDataService) {
    this.openDataService = openDataService;
  }

  public LiveData<ImmutableList<School>> loadMoreSchools(
      int pageSize, FailureHandler failureHandler) {
    ImmutableList<School> current = schools.getValue();
    openDataService.getSchoolList(
        pageSize,
        current.size(),
        results -> {
          ImmutableList<School> updated =
              ImmutableList.<School>builder().addAll(current).addAll(results).build();
          schools.postValue(updated);
        },
        failureHandler);
    return schools;
  }
}
