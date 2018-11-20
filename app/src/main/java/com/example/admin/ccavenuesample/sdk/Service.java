package com.example.admin.ccavenuesample.sdk;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface Service {

    @GET("{path}")
    Call<String> getRsaKey(@Path("path") String path, @QueryMap HashMap<String, String> map);
}


