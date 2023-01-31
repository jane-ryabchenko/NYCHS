package com.jriabchenko.nychs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.network.School;
import com.jriabchenko.nychs.ui.model.SchoolListViewModel;

import java.util.List;

/** Adapter for {@link RecyclerView}. */
public class SchoolListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
              recyclerView.post(() -> loadMore());
            }
          }
        });
    loadMore();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.school_list_row, parent, false);
    return new ItemViewHolder(view, schoolClickHandler);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
    ((ItemViewHolder) viewHolder).setSchool(schools.get(position));
  }

  @Override
  public int getItemCount() {
    return schools == null ? 0 : schools.size();
  }

  private void loadMore() {
    isLoading = true;
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
    private School school;

    public ItemViewHolder(@NonNull View itemView, SchoolClickHandler schoolClickHandler) {
      super(itemView);
      schoolName = itemView.findViewById(R.id.schoolName);
      schoolName.setOnClickListener(view -> schoolClickHandler.onSchoolClick(school));
    }

    public void setSchool(School school) {
      this.school = school;
      schoolName.setText(school.getSchoolName());
    }
  }

  interface SchoolClickHandler {
    void onSchoolClick(School school);
  }
}
