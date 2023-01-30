package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

public class SchoolDetails {
  @Json(name = "school_name")
  String schoolName;

  @Json(name = "overview_paragraph")
  String overviewParagraph;

  @Json(name = "phone_number")
  String phoneNumber;

  @Json(name = "school_email")
  String schoolEmail;

  @Json(name = "website")
  String website;

  @Json(name = "location")
  String location;

  public String getSchoolName() {
    return schoolName;
  }

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
