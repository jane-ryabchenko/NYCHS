package com.jriabchenko.nychs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.databinding.FragmentListBinding;
import com.jriabchenko.nychs.network.NYCHSApiService;
import com.jriabchenko.nychs.network.School;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements SchoolListViewAdapter.SchoolClickHandler {

  private FragmentListBinding binding;
  private RecyclerView recyclerView;
  private SchoolListViewAdapter schoolListViewAdapter;
  private NYCHSApiService api;
  private List<School> rowsArrayList = new ArrayList<>();
  private boolean isLoading = false;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentListBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    api = new NYCHSApiService();
    recyclerView = binding.schoolListView;
    initAdapter();
    initScrollListener();
    loadMore();

    //    binding.buttonFirst.setOnClickListener(
    //        new View.OnClickListener() {
    //          @Override
    //          public void onClick(View view) {
    //            NavHostFragment.findNavController(ListFragment.this)
    //                .navigate(R.id.action_ListFragment_to_DetailsFragment);
    //          }
    //        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void initAdapter() {
    schoolListViewAdapter = new SchoolListViewAdapter(rowsArrayList, this);
    recyclerView.setAdapter(schoolListViewAdapter);
  }

  private void initScrollListener() {
    recyclerView.addOnScrollListener(
        new RecyclerView.OnScrollListener() {
          @Override
          public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
          }

          @Override
          public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!isLoading) {
              if (linearLayoutManager != null
                  && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                      == rowsArrayList.size() - 1) {
                // bottom of list!
                loadMore();
              }
            }
          }
        });
  }

  private void loadMore() {
    isLoading = true;

    int currentSize = rowsArrayList.size();

    rowsArrayList.add(null);
    schoolListViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);

    api.getSchoolList(
        50,
        currentSize,
        schools -> getActivity().runOnUiThread(() -> onSchoolList(schools)),
        error -> {});
  }

  private void onSchoolList(List<School> schools) {
    int lastItemPosition = rowsArrayList.size() - 1;
    rowsArrayList.remove(lastItemPosition);
    schoolListViewAdapter.notifyItemRemoved(lastItemPosition);
    rowsArrayList.addAll(schools);
    schoolListViewAdapter.notifyItemRangeInserted(
        rowsArrayList.size() - schools.size(), schools.size());
    isLoading = false;
  }

  @Override
  public void onSchoolClick(School school) {
    //    Snackbar.make(recyclerView, school.getSchoolName(), LENGTH_SHORT).show();
    Bundle bundle = new Bundle();
    bundle.putString("dbn", school.getDbn());
    NavHostFragment.findNavController(ListFragment.this)
        .navigate(R.id.action_ListFragment_to_DetailsFragment, bundle);
  }
}
