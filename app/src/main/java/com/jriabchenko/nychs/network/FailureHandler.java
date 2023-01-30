package com.jriabchenko.nychs.network;

/** Handles failure. */
public interface FailureHandler {
  void onFailure(Throwable t);
}
