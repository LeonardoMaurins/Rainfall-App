package com.example.rainfallapp.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import androidx.core.app.NotificationCompat;

import com.example.rainfallapp.MainActivity;
import com.example.rainfallapp.forecast.ForecastActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertReceiver extends BroadcastReceiver {

    private String location = MainActivity.returnLocation();

    @Override
    public void onReceive(Context context, Intent intent) {

        apiKey(context, location, "16");
    }

    public void apiKey(final Context context, final String location, final String days) {
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.weatherbit.io/v2.0/forecast/daily?city=" + location + "&days=" + days + "&key=bb134148c3f4410195994fc348f64739")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData = response.body().string();
                    if (!responseData.isEmpty()) {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            JSONArray array = json.getJSONArray("data");

                            int numDays = Integer.parseInt(days);

                            boolean executedBefore = false;

                            for (int i = 0; i < numDays; i++) {
                                JSONObject object = array.getJSONObject(i);

                                JSONObject weatherObject = object.getJSONObject("weather");
                                String description = weatherObject.getString("description");
                                String icon = weatherObject.getString("icon");
                                String code = weatherObject.getString("code");
                                String date = object.getString("datetime");
                                String precipitation = object.getString("precip");

                                int minimumExtent = 1;
                                int weatherExtent = ForecastActivity.checkRainExtent(code);

                                // Compares set rain threshold to extent from JSON
                                // and if it was previously executed
                                if (minimumExtent <= weatherExtent && !executedBefore) {
                                    NotificationHelper notificationHelper = new NotificationHelper(context);
                                    NotificationCompat.Builder nb = notificationHelper.createNotification(location, description, date, icon, precipitation);
                                    notificationHelper.getManager().notify(1, nb.build());
                                    executedBefore = true;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
