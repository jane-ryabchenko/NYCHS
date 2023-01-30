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
  private static final int PAGE_SIZE = 50;

  private final MutableLiveData<List<School>> schools = new MutableLiveData<>(new ArrayList<>());
  private final OpenDataService api = new OpenDataService();

  public LiveData<List<School>> loadMoreSchools(FailureHandler failureHandler) {
    List<School> current = schools.getValue();
    api.getSchoolList(
        PAGE_SIZE,
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
