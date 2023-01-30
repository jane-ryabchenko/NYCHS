package com.jriabchenko.nychs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.network.School;
import com.jriabchenko.nychs.ui.model.SchoolListViewModel;

import java.util.List;

public class SchoolListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;

  private final SchoolListViewModel model;
  private final LifecycleOwner lifecycleOwner;
  private final SchoolClickHandler schoolClickHandler;
  private SnackbarWithRetry snackbar;
  private List<School> schools;
  private boolean isLoading;

  public SchoolListViewAdapter(
      SchoolListViewModel model,
      LifecycleOwner lifecycleOwner,
      SchoolClickHandler schoolClickHandler) {
    this.model = model;
    this.lifecycleOwner = lifecycleOwner;
    this.schoolClickHandler = schoolClickHandler;
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    snackbar = new SnackbarWithRetry(recyclerView);
    recyclerView.addOnScrollListener(
        new RecyclerView.OnScrollListener() {
          @Override
          public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
            // Check if user scrolled to the bottom of the list.
            if (!isLoading
                && linearLayoutManager != null
                && linearLayoutManager.findLastCompletelyVisibleItemPosition()
                    == getItemCount() - 1) {
              isLoading = true;
              loadMore();
            }
          }
        });
    loadMore();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.school_list_row, parent, false);
      return new ItemViewHolder(view);
    }
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.school_list_loading, parent, false);
    return new LoadingViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
    if (viewHolder instanceof ItemViewHolder) {
      populateItemRows((ItemViewHolder) viewHolder, position);
    }
  }

  @Override
  public int getItemCount() {
    return schools == null ? 0 : schools.size();
  }

  @Override
  public int getItemViewType(int position) {
    return schools.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  private void loadMore() {
    model
        .loadMoreSchools(
            error -> snackbar.showError(R.string.error_fetching_school_list, view -> loadMore()))
        .observe(lifecycleOwner, this::setSchoolList);
  }

  public void setSchoolList(List<School> schools) {
    int previousCount = getItemCount();
    this.schools = schools;
    notifyItemRangeInserted(previousCount, schools.size() - previousCount);
    isLoading = false;
  }

  private static class ItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView schoolName;

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);
      schoolName = itemView.findViewById(R.id.schoolName);
    }
  }

  private static class LoadingViewHolder extends RecyclerView.ViewHolder {
    private final ProgressBar progressBar;

    public LoadingViewHolder(@NonNull View itemView) {
      super(itemView);
      progressBar = itemView.findViewById(R.id.progressBar);
    }
  }

  private void populateItemRows(ItemViewHolder viewHolder, int position) {
    School school = schools.get(position);
    viewHolder.schoolName.setText(school.getSchoolName());
    viewHolder.schoolName.setOnClickListener(view -> schoolClickHandler.onSchoolClick(school));
  }

  interface SchoolClickHandler {
    void onSchoolClick(School school);
  }
}
