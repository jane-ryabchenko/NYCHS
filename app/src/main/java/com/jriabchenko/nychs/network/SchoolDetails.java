package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

public class SchoolDetails {
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
}
