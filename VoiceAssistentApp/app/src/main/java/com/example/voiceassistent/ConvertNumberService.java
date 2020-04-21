package com.example.voiceassistent;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConvertNumberService {
    public static ConvertNumberApi getApi() {
        GsonConverterFactory.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ConvertNumberApi.class);
    }
}
