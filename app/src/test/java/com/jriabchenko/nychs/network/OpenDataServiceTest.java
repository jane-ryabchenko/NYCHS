package com.jriabchenko.nychs.network;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/** Unit test for {@link OpenDataService}. */
public class OpenDataServiceTest {
  private static final String APPLICATION_TOKEN = "application_token";
  private static final String DBN_1 = "dbn1";
  private static final School SCHOOL_2 = new School();
  private static final School SCHOOL_3 = new School();
  private static final School SCHOOL_4 = new School();
  private static final SchoolDetails DETAILS_1 = new SchoolDetails();
  private static final SchoolDetails DETAILS_2 = new SchoolDetails();
  private static final SatResults RESULTS_1 = new SatResults();
  private static final SatResults RESULTS_2 = new SatResults();

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock private OpenDataApi openDataApi;
  @Mock private ResponseHandler<ImmutableList<School>> listResponseHandler;
  @Mock private ResponseHandler<SchoolDetails> detailsResponseHandler;
  @Mock private ResponseHandler<ImmutableList<SatResults>> resultsResponseHandler;
  @Mock private FailureHandler failureHandler;

  private OpenDataService service;

  @Before
  public void setUp() {
    service =
        new OpenDataService(
            APPLICATION_TOKEN, openDataApi, MoreExecutors.newDirectExecutorService()) {
          @Override
          protected void logError(Throwable t) {}
        };
  }

  @Test
  public void getSchoolList_validation() {
    // Case 1. Non-positive limit.
    assertThrows(
        IllegalArgumentException.class,
        () -> service.getSchoolList(0, 11, listResponseHandler, failureHandler));

    // Case 2. Negative offset.
    assertThrows(
        IllegalArgumentException.class,
        () -> service.getSchoolList(7, -1, listResponseHandler, failureHandler));
  }

  @Test
  public void getSchoolList_failure() throws IOException {
    Throwable expectedFailure = new IOException();
    mockGetSchoolListFailure(13, 5, expectedFailure);

    service.getSchoolList(13, 5, listResponseHandler, failureHandler);

    verify(failureHandler).onFailure(expectedFailure);
  }

  @Test
  public void getSchoolList_error() throws IOException {
    mockGetSchoolsListError(7, 11, 500);

    service.getSchoolList(7, 11, listResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSchoolList_success() throws IOException {
    mockGetSchoolList(3, 1, SCHOOL_2, SCHOOL_3, SCHOOL_4);

    service.getSchoolList(3, 1, listResponseHandler, failureHandler);

    verify(listResponseHandler).onSuccess(ImmutableList.of(SCHOOL_2, SCHOOL_3, SCHOOL_4));
  }

  @Test
  public void getSchoolDetails_validation() throws IOException {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.getSchoolDetails(/* dbn = */ null, detailsResponseHandler, failureHandler));
  }

  @Test
  public void getSchoolDetails_failure() throws IOException {
    Throwable expectedFailure = new IOException();
    mockGetSchoolDetailsFailure(DBN_1, expectedFailure);

    service.getSchoolDetails(DBN_1, detailsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(expectedFailure);
  }

  @Test
  public void getSchoolDetails_error() throws IOException {
    mockGetSchoolDetailsError(DBN_1, 500);

    service.getSchoolDetails(DBN_1, detailsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSchoolDetails_notFound() throws IOException {
    mockGetSchoolDetails(DBN_1);

    service.getSchoolDetails(DBN_1, detailsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSchoolDetails_nonUnique() throws IOException {
    mockGetSchoolDetails(DBN_1, DETAILS_1, DETAILS_2);

    service.getSchoolDetails(DBN_1, detailsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSchoolDetails_success() throws IOException {
    mockGetSchoolDetails(DBN_1, DETAILS_1);

    service.getSchoolDetails(DBN_1, detailsResponseHandler, failureHandler);

    verify(detailsResponseHandler).onSuccess(DETAILS_1);
  }

  @Test
  public void getSatResults_error() throws IOException {
    mockGetSatResultsError(DBN_1, 500);

    service.getSatResults(DBN_1, resultsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSatResults_nonUnique() throws IOException {
    mockGetSatResults(DBN_1, RESULTS_1, RESULTS_2);

    service.getSatResults(DBN_1, resultsResponseHandler, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void getSatResults_noResults() throws IOException {
    mockGetSatResults(DBN_1);

    service.getSatResults(DBN_1, resultsResponseHandler, failureHandler);

    verify(resultsResponseHandler).onSuccess(ImmutableList.of());
  }

  @Test
  public void getSatResults_success() throws IOException {
    mockGetSatResults(DBN_1, RESULTS_1);

    service.getSatResults(DBN_1, resultsResponseHandler, failureHandler);

    verify(resultsResponseHandler).onSuccess(ImmutableList.of(RESULTS_1));
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolList(int limit, int offset, School... result) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(ImmutableList.copyOf(result)));
    when(openDataApi.getSchoolList(APPLICATION_TOKEN, limit, offset)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolListFailure(int limit, int offset, Throwable t) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSchoolList(APPLICATION_TOKEN, limit, offset)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolsListError(int limit, int offset, int errorCode) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSchoolList(APPLICATION_TOKEN, limit, offset)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetails(String dbn, SchoolDetails... result) throws IOException {
    Call<List<SchoolDetails>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(ImmutableList.copyOf(result)));
    when(openDataApi.getSchoolDetails(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetailsFailure(String dbn, Throwable t) throws IOException {
    Call<List<SchoolDetails>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSchoolDetails(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetailsError(String dbn, int errorCode) throws IOException {
    Call<List<SchoolDetails>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSchoolDetails(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSatResults(String dbn, SatResults... result) throws IOException {
    Call<List<SatResults>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(ImmutableList.copyOf(result)));
    when(openDataApi.getSatResults(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSatResultsFailure(String dbn, Throwable t) throws IOException {
    Call<List<SatResults>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSatResults(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSatResultsError(String dbn, int errorCode) throws IOException {
    Call<List<SatResults>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSatResults(APPLICATION_TOKEN, dbn)).thenReturn(call);
  }
}
