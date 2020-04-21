package com.example.voiceassistent;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConvertNumberApi {
    @GET("/json/convert/num2str")
    Call<ConvertNumber> getConvertNumber(@Query("num") String number);
}
