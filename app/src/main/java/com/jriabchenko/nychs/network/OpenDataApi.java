package com.jriabchenko.nychs.network;

import com.squareup.moshi.Moshi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** Retrofit API for NYC OpenData. */
public interface OpenDataApi {
  String BASE_URL = "https://data.cityofnewyork.us/";

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

  static OpenDataApi createApi() {
    Moshi moshi = new Moshi.Builder().build();
    Retrofit retrofit =
        new Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build();
    return retrofit.create(OpenDataApi.class);
  }
}
