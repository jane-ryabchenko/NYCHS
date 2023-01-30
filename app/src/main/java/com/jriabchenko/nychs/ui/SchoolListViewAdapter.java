package com.jriabchenko.nychs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.network.School;

import java.util.List;

public class SchoolListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;

  private final List<School> schools;
  private final SchoolClickHandler schoolClickHandler;

  public SchoolListViewAdapter(List<School> schools, SchoolClickHandler schoolClickHandler) {
    this.schools = schools;
    this.schoolClickHandler = schoolClickHandler;
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
    } else if (viewHolder instanceof LoadingViewHolder) {
      showLoadingView((LoadingViewHolder) viewHolder, position);
    }
  }

  @Override
  public int getItemCount() {
    return schools == null ? 0 : schools.size();
  }

  /**
   * The following method decides the type of ViewHolder to display in the RecyclerView
   *
   * @param position
   * @return
   */
  @Override
  public int getItemViewType(int position) {
    return schools.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  private class ItemViewHolder extends RecyclerView.ViewHolder {
    TextView schoolName;

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);
      schoolName = itemView.findViewById(R.id.schoolName);
    }
  }

  private class LoadingViewHolder extends RecyclerView.ViewHolder {
    ProgressBar progressBar;

    public LoadingViewHolder(@NonNull View itemView) {
      super(itemView);
      progressBar = itemView.findViewById(R.id.progressBar);
    }
  }

  private void showLoadingView(LoadingViewHolder viewHolder, int position) {
    // ProgressBar would be displayed
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
