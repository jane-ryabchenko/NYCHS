package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

/**
 * Json wrapper for the SAT results data.
 *
 * <p>See <a href="https://data.cityofnewyork.us/Education/2012-SAT-Results/f9bf-2cp4">dataset</a>.
 */
public class SatResults {
  @Json(name = "num_of_sat_test_takers")
  private String numOfSatTestTakers;

  @Json(name = "sat_critical_reading_avg_score")
  private String satCriticalReadingAvgScore;

  @Json(name = "sat_math_avg_score")
  private String satMathAvgScore;

  @Json(name = "sat_writing_avg_score")
  private String satWritingAvgScore;

  public String getNumOfSatTestTakers() {
    return numOfSatTestTakers;
  }

  public String getSatCriticalReadingAvgScore() {
    return satCriticalReadingAvgScore;
  }

  public String getSatMathAvgScore() {
    return satMathAvgScore;
  }

  public String getSatWritingAvgScore() {
    return satWritingAvgScore;
  }
}
