package com.jriabchenko.nychs.network;

/** Handles result of successful response. */
public interface ResponseHandler<T> {
  void onSuccess(T result);
}
