package com.jriabchenko.nychs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.databinding.FragmentListBinding;
import com.jriabchenko.nychs.network.School;
import com.jriabchenko.nychs.ui.model.SchoolListViewModel;

public class ListFragment extends Fragment implements SchoolListViewAdapter.SchoolClickHandler {
  private FragmentListBinding binding;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentListBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SchoolListViewModel model =
        new ViewModelProvider(requireActivity()).get(SchoolListViewModel.class);
    binding.schoolListView.setAdapter(
        new SchoolListViewAdapter(
            model, getViewLifecycleOwner(), /* schoolClickHandler = */ this));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Override
  public void onSchoolClick(School school) {
    Bundle arguments = new Bundle();
    arguments.putString("dbn", school.getDbn());
    arguments.putString("school_name", school.getSchoolName());
    NavHostFragment.findNavController(ListFragment.this)
        .navigate(R.id.action_ListFragment_to_DetailsFragment, arguments);
  }
}
