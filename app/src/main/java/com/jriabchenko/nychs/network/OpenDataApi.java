package com.jriabchenko.nychs.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** Retrofit API for NYC OpenData. */
public interface OpenDataApi {
  @GET("resource/s3k6-pzi2.json?$select=dbn,school_name&$order=school_name")
  Call<List<School>> getSchoolList(
      @Query("$$app_token") String applicationToken,
      @Query("$limit") int limit,
      @Query("$offset") int offset);

  @GET(
      "resource/s3k6-pzi2.json?$select=overview_paragraph,location,phone_number,school_email,"
          + "website")
  Call<List<SchoolDetails>> getSchoolDetails(
      @Query("$$app_token") String applicationToken, @Query("dbn") String dbn);

  @GET(
      "resource/f9bf-2cp4.json?$select=num_of_sat_test_takers,sat_critical_reading_avg_score,"
          + "sat_math_avg_score,sat_writing_avg_score")
  Call<List<SatResults>> getSatResults(
      @Query("$$app_token") String applicationToken, @Query("dbn") String dbn);
}
