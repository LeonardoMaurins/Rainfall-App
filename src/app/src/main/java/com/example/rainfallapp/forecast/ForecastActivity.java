package com.example.rainfallapp.forecast;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rainfallapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastActivity extends AppCompatActivity {

    private int minimumExtent;
    ListView listView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forecast);
        listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        String location = intent.getExtras().getString("location");
        String days = intent.getExtras().getString("days");
        String rain = intent.getExtras().getString("rain");

        switch (rain) {
            case "Light":
                minimumExtent = 1;
                break;
            case "Moderate":
                minimumExtent = 2;
                break;
            case "Heavy":
                minimumExtent = 3;
                break;
            default:
                minimumExtent = 0;
                break;
        }

        TextView daysForecast = findViewById(R.id.daysForecast);
        daysForecast.setText("Days Forecast: " + days);

        apiKey(location, days);
    }

    public void apiKey(final String location, final String days) {
        OkHttpClient client = new OkHttpClient();
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

                            ArrayList<Weather> forecastList = new ArrayList<>();
                            List<Weather> List = new ArrayList<>();

                            boolean executedBefore = false;

                            for (int i = 0; i < numDays; i++) {
                                JSONObject object = array.getJSONObject(i);

                                JSONObject weatherObject = object.getJSONObject("weather");
                                String description = weatherObject.getString("description");
                                String icon = weatherObject.getString("icon");
                                String code = weatherObject.getString("code");
                                String date = object.getString("datetime");
                                String precipitation = object.getString("precip");
                                double Temperature = object.getDouble("temp");
                                String temp = Math.round(Temperature) + "";

                                Weather weather = new Weather(location, description, date, precipitation, temp, "drawable://" + returnDrawable(icon));
                                List.add(weather);
                                forecastList.add(List.get(i));

                                int weatherExtent = checkRainExtent(code);

                                // Compares set rain threshold to extent from JSON
                                // and if it was previously executed
                                if (minimumExtent <= weatherExtent && !executedBefore) {
                                    createNotificationChannel();
                                    createNotification(location, description, date, icon, precipitation);
                                    executedBefore = true;
                                }
                            }
                            WeatherListAdapter adapter = new WeatherListAdapter(ForecastActivity.this, R.layout.adapter_listview_layout, forecastList);
                            setAdapter(listView, adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Invalid details, return to homepage", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(final ListView listView, final WeatherListAdapter adapter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }

    // Checks the codes correlating to the extent of rainfall and returns it
    public static int checkRainExtent(final String code) {
        switch (code) {
            case "200":
            case "230":
            case "300":
            case "500":
            case "520":
            case "523":
            case "600":
                return 1;
            case "201":
            case "231":
            case "301":
            case "501":
            case "521":
            case "601":
            case "610":
            case "611":
                return 2;
            case "202":
            case "232":
            case "233":
            case "302":
            case "502":
            case "511":
            case "522":
            case "602":
            case "612":
                return 3;
            default:
                return 0;
        }
    }

    public static int returnDrawable(final String icon) {
        switch (icon) {
            case "a01d":
                return R.drawable.a01d;
            case "a02d":
                return R.drawable.a02d;
            case "a03d":
                return R.drawable.a03d;
            case "a04d":
                return R.drawable.a04d;
            case "a05d":
                return R.drawable.a05d;
            case "a06d":
                return R.drawable.a06d;
            case "c01d":
                return R.drawable.c01d;
            case "c02d":
                return R.drawable.c02d;
            case "c03d":
                return R.drawable.c03d;
            case "c04d":
                return R.drawable.c04d;
            case "d01d":
                return R.drawable.d01d;
            case "d02d":
                return R.drawable.d02d;
            case "d03d":
                return R.drawable.d03d;
            case "f01d":
                return R.drawable.f01d;
            case "r01d":
                return R.drawable.r01d;
            case "r02d":
                return R.drawable.r02d;
            case "r03d":
                return R.drawable.r03d;
            case "r04d":
                return R.drawable.r04d;
            case "r05d":
                return R.drawable.r05d;
            case "r06d":
                return R.drawable.r06d;
            case "s01d":
                return R.drawable.s01d;
            case "s02d":
                return R.drawable.s02d;
            case "s03d":
                return R.drawable.s03d;
            case "s04d":
                return R.drawable.s04d;
            case "s05d":
                return R.drawable.s05d;
            case "s06d":
                return R.drawable.s06d;
            case "t01d":
                return R.drawable.t01d;
            case "t02d":
                return R.drawable.t02d;
            case "t03d":
                return R.drawable.t03d;
            case "t04d":
                return R.drawable.t04d;
            case "t05d":
                return R.drawable.t05d;
            default:
            case "u00d":
                return R.drawable.u00d;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.notification_id), getString(R.string.notification_title), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(getString(R.string.notification_description));
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // Creates and displays a notification
    private void createNotification(String location, String description, String date, String icon, String precipitation) {
        // Creates intent to display the notification
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Builds the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setSmallIcon(returnDrawable(icon))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), returnDrawable(icon)))
                .setContentTitle(location + " - " + description)
                .setContentText("Date: " + date + ", Precipitation: " + precipitation)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Date: " + date + ", Precipitation: " + precipitation))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(getString(R.string.notification_id))
                .setAutoCancel(true);

        // Adds it as a notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(getResources().getInteger(R.integer.notificationId), builder.build());
    }
}
