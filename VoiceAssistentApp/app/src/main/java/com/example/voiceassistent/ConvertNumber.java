package com.example.voiceassistent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConvertNumber implements Serializable {
    @SerializedName("status")
    @Expose
    public Integer status;

    @SerializedName("str")
    @Expose
    public String str;
}
