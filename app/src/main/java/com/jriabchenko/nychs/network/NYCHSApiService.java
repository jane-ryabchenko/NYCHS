package com.jriabchenko.nychs.network;

import android.util.Log;

import com.squareup.moshi.Moshi;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class NYCHSApiService {
  private static final String BASE_URL = "https://data.cityofnewyork.us/";
  private static final String APPLICATION_TOKEN = "P0GXacjpl2wIpfnW4NMRiXXJN";

  private final NYCHSApi api;

  private final ExecutorService executor;

  public NYCHSApiService() {
    Moshi moshi = new Moshi.Builder().build();
    Retrofit retrofit =
        new Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build();
    api = retrofit.create(NYCHSApi.class);
    executor = Executors.newFixedThreadPool(3);
  }

  public void getSchoolList(
      int limit,
      int offset,
      ResponseHandler<List<School>> responseHandler,
      FailureHandler failureHandler) {
    executeApiCallAsync(
        api.getSchoolList(APPLICATION_TOKEN, limit, offset), responseHandler, failureHandler);
  }

  public void getSchoolDetails(
      String dbn, ResponseHandler<SchoolDetails> responseHandler, FailureHandler failureHandler) {
    executeApiCallAsync(
        api.getSchoolDetails(APPLICATION_TOKEN, dbn),
        results -> responseHandler.onSuccess(singleResult(results)),
        failureHandler);
  }

  public void getSATResults(
      String dbn,
      ResponseHandler<List<SATResults>> responseHandler,
      FailureHandler failureHandler) {
    executeApiCallAsync(
        api.getSATResults(APPLICATION_TOKEN, dbn),
        results -> responseHandler.onSuccess(singleOrNoResult(results)),
        failureHandler);
  }

  private <T> void executeApiCallAsync(
      Call<List<T>> call, ResponseHandler<List<T>> responseHandler, FailureHandler failureHandler) {
    executor.execute(() -> executeApiCall(call, responseHandler, failureHandler));
  }

  private <T> void executeApiCall(
      Call<List<T>> call, ResponseHandler<List<T>> responseHandler, FailureHandler failureHandler) {
    try {
      Response<List<T>> response = call.execute();
      if (!response.isSuccessful()) {
        throw new IllegalStateException(response.message());
      }
      responseHandler.onSuccess(response.body());
    } catch (Throwable t) {
      Log.e(getClass().getSimpleName(), "REST API call error.", t);
      failureHandler.onFailure(t);
    }
  }

  private static <T> T singleResult(List<T> results) {
    if (results.size() != 1) {
      throw new IllegalStateException("Expected a single result, but received " + results.size());
    }
    return results.get(0);
  }

  // Can't use optional due to min version 22.
  private static <T> List<T> singleOrNoResult(List<T> results) {
    if (results.size() > 1) {
      throw new IllegalStateException(
          "Expected a single or no result, but received " + results.size());
    }
    return results;
  }
}
