package com.jriabchenko.nychs.ui.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataApi;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.SatResults;
import com.jriabchenko.nychs.network.SchoolDetails;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/** Unit test for {@link SchoolDetailsViewModel}. */
public class SchoolDetailsViewModelTest {
  private static final String APPLICATION_TOKEN = "application_token";
  private static final String DBN_1 = "dbn1";
  private static final SchoolDetails DETAILS_1 = new SchoolDetails();
  private static final SchoolDetails DETAILS_2 = new SchoolDetails();
  private static final SatResults RESULTS_1 = new SatResults();
  private static final SatResults RESULTS_2 = new SatResults();

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Mock private OpenDataApi openDataApi;
  @Mock private FailureHandler failureHandler;

  private SchoolDetailsViewModel model;

  @Before
  public void setUp() {
    model = new SchoolDetailsViewModel();
    model.setService(
        new OpenDataService(
            APPLICATION_TOKEN, openDataApi, MoreExecutors.newDirectExecutorService()) {
          @Override
          protected void logError(Throwable t) {}
        });
  }

  @Test
  public void loadSchoolDetails_failure() throws IOException {
    Throwable expectedFailure = new IOException();
    mockGetSchoolDetailsFailure(expectedFailure);

    model.loadSchoolDetails(DBN_1, failureHandler);

    verify(failureHandler).onFailure(eq(expectedFailure));
  }

  @Test
  public void loadSchoolDetails_error() throws IOException {
    mockGetSchoolDetailsError(400);

    model.loadSchoolDetails(DBN_1, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadSchoolDetails_notFound() throws IOException {
    mockGetSchoolDetails();

    model.loadSchoolDetails(DBN_1, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadSchoolDetails_notUnique() throws IOException {
    mockGetSchoolDetails(DETAILS_1, DETAILS_2);

    model.loadSchoolDetails(DBN_1, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadSchoolDetails_success() throws IOException {
    mockGetSchoolDetails(DETAILS_1);

    LiveData<SchoolDetails> result = model.loadSchoolDetails(DBN_1, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).isEqualTo(DETAILS_1);
  }

  @Test
  public void loadSatResults_failure() throws IOException {
    Throwable expectedFailure = new IOException();
    mockLoadSatResultsFailure(expectedFailure);

    model.loadSatResults(DBN_1, failureHandler);

    verify(failureHandler).onFailure(eq(expectedFailure));
  }

  @Test
  public void loadSatResults_error() throws IOException {
    mockLoadSatResultsError(400);

    model.loadSatResults(DBN_1, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadSatResults_notUnique() throws IOException {
    mockLoadSatResults(RESULTS_1, RESULTS_2);

    model.loadSatResults(DBN_1, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadSatResults_noResults() throws IOException {
    mockLoadSatResults();

    LiveData<ImmutableList<SatResults>> result = model.loadSatResults(DBN_1, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).isEmpty();
  }

  @Test
  public void loadSatResults_success() throws IOException {
    mockLoadSatResults(RESULTS_1);

    LiveData<ImmutableList<SatResults>> result = model.loadSatResults(DBN_1, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).containsExactly(RESULTS_1);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetails(SchoolDetails... result) throws IOException {
    Call<ImmutableList<SchoolDetails>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(ImmutableList.copyOf(result)));
    when(openDataApi.getSchoolDetails(APPLICATION_TOKEN, DBN_1)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetailsFailure(Throwable t) throws IOException {
    Call<ImmutableList<SchoolDetails>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSchoolDetails(eq(APPLICATION_TOKEN), eq(DBN_1))).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolDetailsError(int errorCode) throws IOException {
    Call<ImmutableList<SchoolDetails>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSchoolDetails(eq(APPLICATION_TOKEN), eq(DBN_1))).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockLoadSatResults(SatResults... result) throws IOException {
    Call<ImmutableList<SatResults>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(ImmutableList.copyOf(result)));
    when(openDataApi.getSatResults(APPLICATION_TOKEN, DBN_1)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockLoadSatResultsFailure(Throwable t) throws IOException {
    Call<ImmutableList<SatResults>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSatResults(eq(APPLICATION_TOKEN), eq(DBN_1))).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockLoadSatResultsError(int errorCode) throws IOException {
    Call<ImmutableList<SatResults>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSatResults(eq(APPLICATION_TOKEN), eq(DBN_1))).thenReturn(call);
  }
}
