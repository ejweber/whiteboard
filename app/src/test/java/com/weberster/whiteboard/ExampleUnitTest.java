package com.weberster.whiteboard;

import android.util.Log;

import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void canGetFingerPaths() {
        // create Retrofit instance
        final String baseURL = "http://192.168.1.211:9876/api/v1/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

        // create API service
        API apiService = retrofit.create(API.class);

        // get and print FingerPaths
        Call<List<FingerPath>> call = apiService.getFingerPaths("test");
        Response<List<FingerPath>> response;
        try {
            response = call.execute();
            System.out.print(response.body().toString());
        }
        catch(java.io.IOException e) {
            System.out.print("found an exception...");
            e.printStackTrace();
        }
    }
}