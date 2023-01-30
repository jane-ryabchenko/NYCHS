package com.jriabchenko.nychs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jriabchenko.nychs.databinding.FragmentDetailsBinding;
import com.jriabchenko.nychs.network.NYCHSApiService;
import com.jriabchenko.nychs.network.SATResults;
import com.jriabchenko.nychs.network.SchoolDetails;

import org.jetbrains.annotations.NotNull;

public class DetailsFragment extends Fragment {

  private FragmentDetailsBinding binding;
  private NYCHSApiService api;

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentDetailsBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    api = new NYCHSApiService();
    loadDetails();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void loadDetails() {
    Bundle arguments = getArguments();
    if (arguments == null) {
      throw new IllegalStateException("Arguments bundle must be set.");
    }
    String dbn = arguments.getString("dbn");
    api.getSchoolDetails(
        dbn, details -> getActivity().runOnUiThread(() -> onDetails(details)), error -> {});
    api.getSATResults(
        dbn, results -> getActivity().runOnUiThread(() -> onSATResults(results)), error -> {});
  }

  private void onDetails(SchoolDetails details) {
    binding.name.setText(details.getSchoolName());
    binding.overview.setText(details.getOverviewParagraph());
    binding.neighborhood.setText(details.getNeighborhood());
  }

  private void onSATResults(SATResults results) {}
}
