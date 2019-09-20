package com.weberster.whiteboard;

import android.support.annotation.ColorInt;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.junit.Test;

import java.lang.reflect.Type;
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
        final String baseURL = "http://192.168.1.2:9876/api/v1/";
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Integer.class, new ColorDeserializer());
        Gson gson = gsonBuilder.create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create(gson)).build();

        // create API service
        API apiService = retrofit.create(API.class);

        // get and print FingerPaths
        Call<List<FingerPath>> call = apiService.getFingerPaths("test_board");
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