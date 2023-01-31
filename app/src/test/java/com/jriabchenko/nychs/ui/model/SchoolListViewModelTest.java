package com.jriabchenko.nychs.ui.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.MoreExecutors;
import com.jriabchenko.nychs.network.FailureHandler;
import com.jriabchenko.nychs.network.OpenDataApi;
import com.jriabchenko.nychs.network.OpenDataService;
import com.jriabchenko.nychs.network.School;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/** Unit test for {@link SchoolListViewModel}. */
public class SchoolListViewModelTest {
  private static final String APPLICATION_TOKEN = "application_token";
  private static final int PAGE_SIZE = 2;
  private static final School SCHOOL_1 = new School();
  private static final School SCHOOL_2 = new School();
  private static final School SCHOOL_3 = new School();
  private static final School SCHOOL_4 = new School();

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Mock private OpenDataApi openDataApi;
  @Mock private FailureHandler failureHandler;

  private SchoolListViewModel model;

  @Before
  public void setUp() {
    model = new SchoolListViewModel();
    model.setService(
        new OpenDataService(
            APPLICATION_TOKEN, openDataApi, MoreExecutors.newDirectExecutorService()) {
          @Override
          protected void logError(Throwable t) {}
        });
  }

  @Test
  public void loadMoreSchools_failure() throws IOException {
    Throwable expectedFailure = new IOException();
    mockGetSchoolListFailure(expectedFailure);

    model.loadMoreSchools(PAGE_SIZE, failureHandler);

    verify(failureHandler).onFailure(eq(expectedFailure));
  }

  @Test
  public void loadMoreSchools_error() throws IOException {
    mockGetSchoolsListError(400);

    model.loadMoreSchools(PAGE_SIZE, failureHandler);

    verify(failureHandler).onFailure(isA(IllegalStateException.class));
  }

  @Test
  public void loadMoreSchools_success() throws IOException {
    mockGetSchoolListResult(0, SCHOOL_1, SCHOOL_2);
    mockGetSchoolListResult(2, SCHOOL_3, SCHOOL_4);
    mockGetSchoolListResult(4);

    // Step 1. Load first page
    LiveData<List<School>> result = model.loadMoreSchools(PAGE_SIZE, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).containsExactly(SCHOOL_1, SCHOOL_2).inOrder();

    // Step 2. Load second page
    result = model.loadMoreSchools(PAGE_SIZE, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).containsExactly(SCHOOL_1, SCHOOL_2, SCHOOL_3, SCHOOL_4).inOrder();

    // Step 3. Load third page
    result = model.loadMoreSchools(PAGE_SIZE, failureHandler);

    result.observeForever(data -> {});
    assertThat(result.getValue()).containsExactly(SCHOOL_1, SCHOOL_2, SCHOOL_3, SCHOOL_4).inOrder();
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolListResult(int offset, School... result) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute()).thenReturn(Response.success(Arrays.asList(result)));
    when(openDataApi.getSchoolList(APPLICATION_TOKEN, PAGE_SIZE, offset)).thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolListFailure(Throwable t) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute()).thenThrow(t);
    when(openDataApi.getSchoolList(eq(APPLICATION_TOKEN), eq(PAGE_SIZE), anyInt()))
        .thenReturn(call);
  }

  @SuppressWarnings("unchecked")
  private void mockGetSchoolsListError(int errorCode) throws IOException {
    Call<List<School>> call = mock(Call.class);
    when(call.execute())
        .thenReturn(
            Response.error(errorCode, ResponseBody.create(MediaType.get("text/plain"), "Error")));
    when(openDataApi.getSchoolList(eq(APPLICATION_TOKEN), eq(PAGE_SIZE), anyInt()))
        .thenReturn(call);
  }
}
