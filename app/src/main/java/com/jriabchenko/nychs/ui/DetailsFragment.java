package com.jriabchenko.nychs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.databinding.FragmentDetailsBinding;
import com.jriabchenko.nychs.network.SATResults;
import com.jriabchenko.nychs.network.SchoolDetails;
import com.jriabchenko.nychs.ui.model.SchoolDetailsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailsFragment extends Fragment {
  private static final int MAX_RETRIES_NUMBER = 3;

  private final AtomicInteger successCounter = new AtomicInteger(0);
  private final AtomicInteger retryCounter = new AtomicInteger(0);
  private FragmentDetailsBinding binding;
  private SchoolDetailsViewModel model;

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentDetailsBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, /* savedInstanceState = */ null);
    model = new ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel.class);

    Bundle arguments = getArguments();
    if (arguments == null) {
      throw new IllegalStateException("Arguments bundle must be set.");
    }
    String dbn = arguments.getString("dbn");
    binding.name.setText(arguments.getString("school_name"));

    successCounter.set(0);
    retryCounter.lazySet(0);

    fetchDetails(dbn);
    fetchResults(dbn);

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding.progressBar.setVisibility(View.VISIBLE);
    binding.schoolDetails.setVisibility(View.GONE);
    binding = null;
  }

  private void fetchDetails(String dbn) {
    model
        .loadSchoolDetails(
            dbn,
            error -> showError(R.string.error_fetching_school_details, view -> fetchDetails(dbn)))
        .observe(getViewLifecycleOwner(), this::setSchoolDetails);
  }

  private void fetchResults(String dbn) {
    model
        .loadSATResults(
            dbn,
            error -> showError(R.string.error_fetching_sat_results, view -> fetchResults(dbn)))
        .observe(getViewLifecycleOwner(), this::setSATResults);
  }

  private void setSchoolDetails(SchoolDetails details) {
    binding.name.setText(details.getSchoolName());
    binding.overview.setText(details.getOverviewParagraph());
    binding.phoneNumber.setText(details.getPhoneNumber());
    binding.emailAddress.setText(details.getSchoolEmail());
    binding.websiteAddress.setText(details.getWebsite());
    binding.location.setText(details.getLocation());
    maybeShowContent();
  }

  private void setSATResults(List<SATResults> resultList) {
    if (resultList.isEmpty()) {
      binding.numOfSatTestTakers.setText(R.string.sat_results_no_data);
      binding.satMathAvgScore.setText(R.string.sat_results_no_data);
      binding.satCriticalReadingAvgScore.setText(R.string.sat_results_no_data);
      binding.satWritingAvgScore.setText(R.string.sat_results_no_data);
    } else {
      SATResults results = resultList.get(0);
      binding.numOfSatTestTakers.setText(results.getNumOfSatTestTakers());
      binding.satMathAvgScore.setText(results.getSatMathAvgScore());
      binding.satCriticalReadingAvgScore.setText(results.getSatCriticalReadingAvgScore());
      binding.satWritingAvgScore.setText(results.getSatWritingAvgScore());
    }
    maybeShowContent();
  }

  private void maybeShowContent() {
    if (successCounter.incrementAndGet() == 2) {
      binding.progressBar.setVisibility(View.GONE);
      binding.schoolDetails.setVisibility(View.VISIBLE);
    }
  }

  private void showError(@StringRes int id, View.OnClickListener onRetryListener) {
    Snackbar snackbar = Snackbar.make(requireView(), id, BaseTransientBottomBar.LENGTH_INDEFINITE);
    if (retryCounter.get() < MAX_RETRIES_NUMBER) {
      snackbar.setAction(
          R.string.error_button_retry,
          view -> {
            retryCounter.incrementAndGet();
            onRetryListener.onClick(view);
          });
    }
    snackbar.show();
  }
}
