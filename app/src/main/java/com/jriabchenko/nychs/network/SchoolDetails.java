package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

public class SchoolDetails {
  @Json(name = "school_name")
  String schoolName;

  @Json(name = "overview_paragraph")
  String overviewParagraph;

  @Json(name = "neighborhood")
  String neighborhood;

  @Json(name = "location")
  String location;

  @Json(name = "phone_number")
  String phoneNumber;

  @Json(name = "fax_number")
  String faxNumber;

  @Json(name = "school_email")
  String schoolEmail;

  @Json(name = "website")
  String website;

  @Json(name = "total_students")
  int totalStudents;

  public String getSchoolName() {
    return schoolName;
  }

  public String getOverviewParagraph() {
    return overviewParagraph;
  }

  public String getNeighborhood() {
    return neighborhood;
  }

  public String getLocation() {
    return location;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getFaxNumber() {
    return faxNumber;
  }

  public String getSchoolEmail() {
    return schoolEmail;
  }

  public String getWebsite() {
    return website;
  }

  public int getTotalStudents() {
    return totalStudents;
  }
}
