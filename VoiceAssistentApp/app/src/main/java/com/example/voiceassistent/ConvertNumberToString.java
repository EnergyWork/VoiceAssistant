package com.example.voiceassistent;

import android.util.Log;
import java.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertNumberToString {
    public static void getConvertNumber(String number, final Consumer<String> callback) {
        ConvertNumberApi api = ConvertNumberService.getApi();
        Call<ConvertNumber> call = api.getConvertNumber(number);
        call.enqueue(new Callback<ConvertNumber>() {
            @Override
            public void onResponse(Call<ConvertNumber> call, Response<ConvertNumber> response) {
                ConvertNumber res = response.body();
                if (res != null) {
                    if (res.status == 200) {
                        callback.accept(res.str);
                    } else {
                        callback.accept("Неверный параметр! Статус: " + res.status);
                    }
                } else {
                    callback.accept("Не могу конвертировать число!");
                }
            }

            @Override
            public void onFailure(Call<ConvertNumber> call, Throwable t) {
                Log.w("CONVERTNUMBER", t.getMessage());
            }
        });
    }
}
