package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

/**
 * Json wrapper for the school data.
 *
 * <p>More detailed version of {@link School} wrapper. It is used in "details" fragment where larger
 * amount of information is displayed.
 *
 * <p>See <a
 * href="https://data.cityofnewyork.us/Education/2017-DOE-High-School-Directory/s3k6-pzi2">dataset</a>.
 */
public class SchoolDetails {
  @Json(name = "overview_paragraph")
  private String overviewParagraph;

  @Json(name = "phone_number")
  private String phoneNumber;

  @Json(name = "school_email")
  private String schoolEmail;

  @Json(name = "website")
  private String website;

  @Json(name = "location")
  private String location;

  public String getOverviewParagraph() {
    return overviewParagraph;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getSchoolEmail() {
    return schoolEmail;
  }

  public String getWebsite() {
    return website;
  }

  public String getLocation() {
    return location;
  }
}
