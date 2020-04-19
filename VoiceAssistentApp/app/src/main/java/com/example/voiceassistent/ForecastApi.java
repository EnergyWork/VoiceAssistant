package com.example.voiceassistent;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi { //571436e191fba0eea41ff55bafa17bd9
    @GET("/current?access_key=571436e191fba0eea41ff55bafa17bd9")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
