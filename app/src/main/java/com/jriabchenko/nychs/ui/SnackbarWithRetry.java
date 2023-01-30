package com.jriabchenko.nychs.ui;

import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jriabchenko.nychs.R;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class SnackbarWithRetry {
  private static final int MAX_RETRIES_NUMBER = 3;

  private final AtomicInteger retryCounter = new AtomicInteger(0);
  private final View view;

  public SnackbarWithRetry(@NotNull View view) {
    this.view = view;
  }

  public void reset() {
   retryCounter.set(0);
  }

  public void showError(@StringRes int id, View.OnClickListener onRetryListener) {
    Snackbar snackbar = Snackbar.make(view, id, BaseTransientBottomBar.LENGTH_INDEFINITE);
    if (retryCounter.get() < MAX_RETRIES_NUMBER) {
      snackbar.setAction(
          R.string.error_button_retry,
          view -> {
            retryCounter.incrementAndGet();
            onRetryListener.onClick(view);
          });
    }
    snackbar.show();
  }
}
