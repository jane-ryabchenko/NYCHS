package com.jriabchenko.nychs.network;

import com.squareup.moshi.Moshi;

import java.io.IOException;
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
        api.getSchoolDetails(APPLICATION_TOKEN, dbn), responseHandler, failureHandler);
  }

  public void getSATResults(
      String dbn, ResponseHandler<SATResults> responseHandler, FailureHandler failureHandler) {
    executeApiCallAsync(api.getSATResults(APPLICATION_TOKEN, dbn), responseHandler, failureHandler);
  }

  private <T> void executeApiCallAsync(
      Call<T> call, ResponseHandler<T> responseHandler, FailureHandler failureHandler) {
    executor.execute(() -> executeApiCall(call, responseHandler, failureHandler));
  }

  private <T> void executeApiCall(
      Call<T> call, ResponseHandler<T> responseHandler, FailureHandler failureHandler) {
    try {
      Response<T> response = call.execute();
      if (!response.isSuccessful()) {
        throw new IllegalStateException(response.message());
      }
      responseHandler.onSuccess(response.body());
    } catch (IOException e) {
      failureHandler.onFailure(e);
    }
  }
}
