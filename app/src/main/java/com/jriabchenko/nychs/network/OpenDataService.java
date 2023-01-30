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

/** Service for High Schools and SAT scores data from NYC OpenData API. */
public class OpenDataService {
  private static final String BASE_URL = "https://data.cityofnewyork.us/";
  private static final String APPLICATION_TOKEN = "P0GXacjpl2wIpfnW4NMRiXXJN";

  private final OpenDataApi api;

  private final ExecutorService executor;

  public OpenDataService() {
    Moshi moshi = new Moshi.Builder().build();
    Retrofit retrofit =
        new Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build();
    api = retrofit.create(OpenDataApi.class);
    executor = Executors.newFixedThreadPool(3);
  }

  /**
   * Fetches school list with pagination.
   *
   * <p>Returned results are ordered by school name. Data is limited to school name and unique
   * identifier to reduce network payload.
   */
  public void getSchoolList(
      int limit,
      int offset,
      ResponseHandler<List<School>> responseHandler,
      FailureHandler failureHandler) {
    if (limit < 1) {
      throw new IllegalArgumentException("Limit should be positive.");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("Offset should be non-negative.");
    }
    executeApiCallAsync(
        api.getSchoolList(APPLICATION_TOKEN, limit, offset), responseHandler, failureHandler);
  }

  /**
   * Fetches detailed information for a specific school identified by {@code dbn}.
   *
   * <p>We always expect exactly one result being returned. Having no, or more than two results
   * indicates either problem on the server side or a bug on the client side (wrong dbn, for ex).
   */
  public void getSchoolDetails(
      String dbn, ResponseHandler<SchoolDetails> responseHandler, FailureHandler failureHandler) {
    executeApiCallAsync(
        api.getSchoolDetails(APPLICATION_TOKEN, dbn),
        results -> responseHandler.onSuccess(singleResult(results)),
        failureHandler);
  }

  /**
   * Fetches SAT results for a specific school identified by {@code dbn}, if available.
   *
   * <p>For some school this information may not be available. This is not necessary due to a server
   * side or client side issue.
   */
  public void getSatResults(
      String dbn,
      ResponseHandler<List<SatResults>> responseHandler,
      FailureHandler failureHandler) {
    executeApiCallAsync(
        api.getSatResults(APPLICATION_TOKEN, dbn),
        results -> responseHandler.onSuccess(singleOrNoResult(results)),
        failureHandler);
  }

  /** Async wrapper for the call. */
  private <T> void executeApiCallAsync(
      Call<List<T>> call, ResponseHandler<List<T>> responseHandler, FailureHandler failureHandler) {
    executor.execute(() -> executeApiCall(call, responseHandler, failureHandler));
  }

  /** Executes call and handles result or failure. */
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

  /** Verifies that exactly single result was returned. */
  private static <T> T singleResult(List<T> results) {
    if (results.size() != 1) {
      throw new IllegalStateException("Expected a single result, but received " + results.size());
    }
    return results.get(0);
  }

  /** Verifies that single or no result was returned. */
  private static <T> List<T> singleOrNoResult(List<T> results) {
    // Can't use optional due to min version 22.
    if (results.size() > 1) {
      throw new IllegalStateException(
          "Expected a single or no result, but received " + results.size());
    }
    return results;
  }
}
