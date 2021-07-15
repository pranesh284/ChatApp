package com.example.chatapp.Fragments;

import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHecUoi8:APA91bGJ1mRVoj2qRx_DniMZ7FSu8IoJrzRV2PTLeQn98uCihZZstDsUoBAcSMGQduxBi7oXKhzhS3BIcEYZd-kGMgjYiRRAitipzIbhlDTi-oOji6ecUEL5FGATc22Fyexbc4Z3p04C"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifacation(@Body Sender body);
}
