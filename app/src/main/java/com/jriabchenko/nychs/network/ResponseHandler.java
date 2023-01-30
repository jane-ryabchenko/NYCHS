package com.jriabchenko.nychs.network;

public interface ResponseHandler<T> {
  void onSuccess(T result);
  //    void onSchoolListSuccess(List<School> schools);
  //    void onSchoolDetailsSuccess(SchoolDetails details);
  //    void onSATResultsSuccess(SATResults results);
  //
  //    void onSchoolListFailure(Throwable t);
  //    void onSchoolDetailsFailure(Throwable t);
  //    void onSATResultsFailure(Throwable t);
}
