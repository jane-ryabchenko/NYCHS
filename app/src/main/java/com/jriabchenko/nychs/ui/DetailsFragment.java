package com.jriabchenko.nychs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.databinding.FragmentDetailsBinding;
import com.jriabchenko.nychs.network.SatResults;
import com.jriabchenko.nychs.network.SchoolDetails;
import com.jriabchenko.nychs.ui.model.SchoolDetailsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Fragment displaying a recycle view with "infinite scroll. */
public class DetailsFragment extends Fragment {
  private final AtomicInteger responseCounter = new AtomicInteger(0);

  private FragmentDetailsBinding binding;
  private SchoolDetailsViewModel model;
  private SnackbarWithRetry snackbar;

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentDetailsBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    model = new ViewModelProvider(requireActivity()).get(SchoolDetailsViewModel.class);
    snackbar = new SnackbarWithRetry(view);

    Bundle arguments = getArguments();
    if (arguments == null) {
      throw new IllegalStateException("Arguments bundle must be set.");
    }
    String dbn = arguments.getString("dbn");
    binding.name.setText(arguments.getString("school_name"));

    responseCounter.set(0);

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

  /** Attempt to fetch school details, allowing user to retry up to 3 times combined. */
  private void fetchDetails(String dbn) {
    model
        .loadSchoolDetails(
            dbn,
            error ->
                snackbar.showError(
                    R.string.error_fetching_school_details, view -> fetchDetails(dbn)))
        .observe(getViewLifecycleOwner(), this::setSchoolDetails);
  }

  /** Attempt to fetch SAT results, allowing user to retry up to 3 times combined. */
  private void fetchResults(String dbn) {
    model
        .loadSatResults(
            dbn,
            error ->
                snackbar.showError(R.string.error_fetching_sat_results, view -> fetchResults(dbn)))
        .observe(getViewLifecycleOwner(), this::setSatResults);
  }

  private void setSchoolDetails(SchoolDetails details) {
    binding.overview.setText(details.getOverviewParagraph());
    binding.phoneNumber.setText(details.getPhoneNumber());
    binding.emailAddress.setText(details.getSchoolEmail());
    binding.websiteAddress.setText(details.getWebsite());
    binding.location.setText(details.getLocation());
    maybeShowContent();
  }

  private void setSatResults(List<SatResults> resultList) {
    if (resultList.isEmpty()) {
      binding.numOfSatTestTakers.setText(R.string.sat_results_no_data);
      binding.satMathAvgScore.setText(R.string.sat_results_no_data);
      binding.satCriticalReadingAvgScore.setText(R.string.sat_results_no_data);
      binding.satWritingAvgScore.setText(R.string.sat_results_no_data);
    } else {
      SatResults results = resultList.get(0);
      binding.numOfSatTestTakers.setText(results.getNumOfSatTestTakers());
      binding.satMathAvgScore.setText(results.getSatMathAvgScore());
      binding.satCriticalReadingAvgScore.setText(results.getSatCriticalReadingAvgScore());
      binding.satWritingAvgScore.setText(results.getSatWritingAvgScore());
    }
    maybeShowContent();
  }

  private void maybeShowContent() {
    if (responseCounter.incrementAndGet() == 2) {
      binding.progressBar.setVisibility(View.GONE);
      binding.schoolDetails.setVisibility(View.VISIBLE);
    }
  }
}
