package com.mukeshkpdeveloper.taskmap.networking;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {
    @GET("viewreport")
    Call<JsonObject> getViewport();


}