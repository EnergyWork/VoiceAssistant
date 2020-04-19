package com.example.voiceassistent;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastService {
    public static ForecastApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com")
                .addConverterFactory(GsonConverterFactory.create().create())
                .build();
        return retrofit.create(ForecastApi.class);
    }
}
