package com.jriabchenko.nychs.network;

import com.squareup.moshi.Json;

public class SATResults {
    @Json(name = "num_of_sat_test_takers") int numOfSatTestTakers;
    @Json(name = "sat_critical_reading_avg_score") String satCriticalReadingAvgScore;
    @Json(name = "sat_math_avg_score") String satMathAvgScore;
    @Json(name = "sat_writing_avg_score") String satWritingAvgScore;

    public int getNumOfSatTestTakers() {
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
