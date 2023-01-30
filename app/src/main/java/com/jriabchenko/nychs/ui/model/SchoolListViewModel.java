package com.jriabchenko.nychs.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.NYCHSApiService;
import com.jriabchenko.nychs.network.School;

import java.util.ArrayList;
import java.util.List;

public class SchoolListViewModel extends ViewModel {
  private static final int PAGE_SIZE = 50;

  private final MutableLiveData<List<School>> schools = new MutableLiveData<>(new ArrayList<>());
  private final NYCHSApiService api = new NYCHSApiService();

  public LiveData<List<School>> loadMoreSchools(FailureHandler failureHandler) {
    List<School> current = schools.getValue();
    api.getSchoolList(
        PAGE_SIZE,
        current.size(),
        results -> {
          List<School> updated = new ArrayList<>();
          updated.addAll(current);
          updated.addAll(results);
          schools.postValue(updated);
        },
        failureHandler);
    return schools;
  }
}
