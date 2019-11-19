package com.example.mymoviepartner.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQiDaX44:APA91bH9UanIFGx6aBk7W2Y9PqjusYdOb2CuZ6KuoJr6f1y1eGcHI8p84msr0KN3ztAmg8e8xUTGKM5cvhUz7QboISmTAlajQ8-f5zRld6iahNYTwSzpvDs-aMf_imhjHbI2y_mi_8WW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
