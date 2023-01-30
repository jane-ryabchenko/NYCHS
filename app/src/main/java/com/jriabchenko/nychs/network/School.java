package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

/**
 * Json wrapper for the school data.
 *
 * <p>This wrapper is intentionally limited to keep it as lite as possible. It is used in "infinite"
 * scroll view, thus having as fewer number of fields as possible is better for the network and
 * memory performance.
 *
 * <p>See <a
 * href="https://data.cityofnewyork.us/Education/2017-DOE-High-School-Directory/s3k6-pzi2">dataset</a>.
 */
public class School {
  @Json(name = "dbn")
  private String dbn;

  @Json(name = "school_name")
  private String schoolName;

  public String getDbn() {
    return dbn;
  }

  public String getSchoolName() {
    return schoolName;
  }
}
