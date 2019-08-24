package com.weberster.whiteboard;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface API {
    @GET("boards/{boardName}/fingerpaths")
    Call<List<FingerPath>> getFingerPaths(@Path("boardName") String boardName);

    @POST("boards/{boardName}/fingerpaths")
    Call<ResponseBody> postFingerPaths(@Path("boardName") String boardName, @Body List<FingerPath> fingerPaths);
}
