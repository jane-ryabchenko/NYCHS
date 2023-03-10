package com.jriabchenko.nychs.network;

import static com.jriabchenko.nychs.network.OpenDataApi.createApi;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

/** Service for High Schools and SAT scores data from NYC OpenData API. */
public class OpenDataService {

  private final String applicationToken;
  private final OpenDataApi api;
  private final ExecutorService executor;

  public OpenDataService(String applicationToken) {
    this(applicationToken, createApi(), Executors.newFixedThreadPool(3));
  }

  @VisibleForTesting
  public OpenDataService(String applicationToken, OpenDataApi api, ExecutorService executor) {
    this.applicationToken = applicationToken;
    this.api = api;
    this.executor = executor;
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
      ResponseHandler<ImmutableList<School>> responseHandler,
      FailureHandler failureHandler) {
    if (limit < 1) {
      throw new IllegalArgumentException("Limit should be positive.");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("Offset should be non-negative.");
    }
    executeApiCallAsync(
        api.getSchoolList(applicationToken, limit, offset), responseHandler, failureHandler);
  }

  /**
   * Fetches detailed information for a specific school identified by {@code dbn}.
   *
   * <p>We always expect exactly one result being returned. Having no, or more than two results
   * indicates either problem on the server side or a bug on the client side (wrong dbn, for ex).
   */
  public void getSchoolDetails(
      String dbn, ResponseHandler<SchoolDetails> responseHandler, FailureHandler failureHandler) {
    if (dbn == null) {
      throw new IllegalArgumentException("DBN should not be null.");
    }
    executeApiCallAsync(
        api.getSchoolDetails(applicationToken, dbn),
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
      ResponseHandler<ImmutableList<SatResults>> responseHandler,
      FailureHandler failureHandler) {
    if (dbn == null) {
      throw new IllegalArgumentException("DBN should not be null.");
    }
    executeApiCallAsync(
        api.getSatResults(applicationToken, dbn),
        results -> responseHandler.onSuccess(singleOrNoResult(results)),
        failureHandler);
  }

  /** Async wrapper for the call. */
  private <T> void executeApiCallAsync(
      Call<List<T>> call,
      ResponseHandler<ImmutableList<T>> responseHandler,
      FailureHandler failureHandler) {
    executor.execute(() -> executeApiCall(call, responseHandler, failureHandler));
  }

  /** Executes call and handles result or failure. */
  private <T> void executeApiCall(
      Call<List<T>> call,
      ResponseHandler<ImmutableList<T>> responseHandler,
      FailureHandler failureHandler) {
    try {
      Response<List<T>> response = call.execute();
      if (!response.isSuccessful()) {
        throw new IllegalStateException(response.message());
      }
      responseHandler.onSuccess(ImmutableList.copyOf(response.body()));
    } catch (Throwable t) {
      logError(t);
      failureHandler.onFailure(t);
    }
  }

  @VisibleForTesting
  protected void logError(Throwable t) {
    Log.e(getClass().getSimpleName(), "REST API call error.", t);
  }

  /** Verifies that exactly single result was returned. */
  private static <T> T singleResult(ImmutableList<T> results) {
    if (results.size() != 1) {
      throw new IllegalStateException("Expected a single result, but received " + results.size());
    }
    return results.get(0);
  }

  /** Verifies that single or no result was returned. */
  private static <T> ImmutableList<T> singleOrNoResult(ImmutableList<T> results) {
    // Can't use optional due to min version 22.
    if (results.size() > 1) {
      throw new IllegalStateException(
          "Expected a single or no result, but received " + results.size());
    }
    return results;
  }
}
