package com.example.myapplication.notifications;

import android.content.Context;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.datatransport.backend.cct.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationWorker extends Worker {

    private String token;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork(){
        token = getInputData().getString("token");
        sendMessage("Hello"); //for now it sends a notification every hour
        return Result.success();
    }


    public void sendMessage(String body)  {

        try{
            JSONObject json  = new JSONObject();
            JSONObject notification = new JSONObject();

            notification.put("title","Test");
            notification.put("body", body);


            json.put("notification",notification);
            json.put("to",token);

            callAPI(json);
        } catch (Exception e){

        }


    }

    public void callAPI(JSONObject jsonObject){
        //sends api call to firebase to send a message
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String URL = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder().url(URL).post(body).header("Authorization", "Bearer AAAA66SfK_Q:APA91bGnptafWF1eotnS01oc7VJJmrvXeyxj5o2lxPWKLFSFCEoQIcSLXJYaFIoR-oA1l5xhHmloAXJ01iisIZf9ij9fL7gOvwHQv7jdOOgMMDqK3NqNQXzoxi1DsQ3Z8_eAkYGmuaLW").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}
