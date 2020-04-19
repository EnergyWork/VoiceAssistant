package com.example.voiceassistent;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(String city, final Consumer<String> callback) {
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(@NonNull Call<Forecast> call, @NonNull Response<Forecast> response) {
                Forecast result = response.body();
                if (result != null) {
                    String answer =
                            "Сейчас где-то " + result.current.temperature +
                            " градус" + AI.getGradusEnding(result.current.temperature) +
                            " и " + result.current.weather_descriptions.get(0);
                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу узнать погоду :(");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Forecast> call, @NonNull Throwable t) {
                Log.w("WEATHER", t.getMessage());
            }
        });
    }
}
