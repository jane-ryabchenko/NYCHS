package com.jriabchenko.nychs.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jriabchenko.nychs.R;
import com.jriabchenko.nychs.databinding.ActivitySchoolBinding;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.ui.model.SchoolDetailsViewModel;
import com.jriabchenko.nychs.ui.model.SchoolListViewModel;

/** Main activity of the app. */
public class SchoolActivity extends AppCompatActivity {
  private static final String APPLICATION_TOKEN = "P0GXacjpl2wIpfnW4NMRiXXJN";

  private AppBarConfiguration appBarConfiguration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    OpenDataService openDataService = new OpenDataService(APPLICATION_TOKEN);
    SchoolDetailsViewModel detailsViewModel =
        new ViewModelProvider(this).get(SchoolDetailsViewModel.class);
    detailsViewModel.setService(openDataService);

    SchoolListViewModel listViewModel = new ViewModelProvider(this).get(SchoolListViewModel.class);
    listViewModel.setService(openDataService);

    ActivitySchoolBinding binding = ActivitySchoolBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    setSupportActionBar(binding.toolbar);

    NavController navController =
        Navigation.findNavController(this, R.id.nav_host_fragment_content_school);
    appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavController navController =
        Navigation.findNavController(this, R.id.nav_host_fragment_content_school);
    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }
}
