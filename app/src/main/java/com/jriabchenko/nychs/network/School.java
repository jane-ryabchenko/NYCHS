package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

import javax.annotation.Nullable;

public class School {
  @Json(name = "dbn")
  String dbn;

  @Json(name = "school_name")
  String schoolName;

  @Json(ignore = true)
  @Nullable
  SchoolDetails schoolDetails;

  @Json(ignore = true)
  @Nullable
  SATResults satResults;

  public String getDbn() {
    return dbn;
  }

  public String getSchoolName() {
    return schoolName;
  }

  @Nullable
  public SchoolDetails getSchoolDetails() {
    return schoolDetails;
  }

  @Nullable
  public SATResults getSatResults() {
    return satResults;
  }
}
