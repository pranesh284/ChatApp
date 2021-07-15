package com.example.chatapp.Notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit = null;
    public static Retrofit getClient(String uri)
    {
        if(retrofit == null)
            return new Retrofit.Builder().baseUrl(uri).
                    addConverterFactory
                    (GsonConverterFactory.create()).build();

        return retrofit;
    }
}
