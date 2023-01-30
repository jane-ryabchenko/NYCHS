package com.jriabchenko.nychs.ui;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.network.NYCHSApiService;
import com.jriabchenko.nychs.network.School;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
    implements SchoolListViewAdapter.SchoolClickHandler {
  RecyclerView recyclerView;
  SchoolListViewAdapter schoolListViewAdapter;
  NYCHSApiService api;
  List<School> rowsArrayList = new ArrayList<>();

  boolean isLoading = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    api = new NYCHSApiService();
    recyclerView = findViewById(R.id.schoolListView);
    initAdapter();
    initScrollListener();
    loadMore();
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
        50, currentSize, schools -> runOnUiThread(() -> onSchoolList(schools)), error -> {});
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
    Snackbar.make(recyclerView, school.getSchoolName(), LENGTH_SHORT).show();
//    Intent intent = new Intent(this, DetailsActivity.class);
//    startActivity(intent);
  }
}
