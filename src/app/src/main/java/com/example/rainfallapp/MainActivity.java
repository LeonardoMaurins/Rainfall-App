package com.example.rainfallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rainfallapp.alarm.AlarmActivity;
import com.example.rainfallapp.forecast.ForecastActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String SHARED_PREFERENCES = "sharedPrefs";
    private String LOCATION = "location";
    private String DAYS = "days";
    private String RAIN = "rain";

    private TextView editTextLocation, editTextDays;
    private Button buttonLocation, buttonForecast, buttonPreferences, buttonAlarm;
    private Spinner dropdown;
    private String[] rainThresholds = new String[]{"Light", "Moderate", "Heavy"};
    ;

    private static String location;
    private String days;
    private int rain;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buttonLocation = findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(this);
        buttonForecast = findViewById(R.id.buttonForecast);
        buttonForecast.setOnClickListener(this);
        buttonPreferences = findViewById(R.id.buttonPreferences);
        buttonPreferences.setOnClickListener(this);
        buttonAlarm = findViewById(R.id.buttonAlarm);
        buttonAlarm.setOnClickListener(this);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDays = findViewById(R.id.editTextDays);

        dropdown = findViewById(R.id.spinner);
        // Adapter to define how the strings are displayed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, rainThresholds);
        dropdown.setAdapter(adapter);

        // Loading previous preferences and updating the view
        loadPrefs();
        updatePrefs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // When permission granted call method
            getLocation();
        } else {
            // When permission is denied, display toast
            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initializing location
                Location location = task.getResult();
                if (location != null) {
                    // Initializing Geocoder
                    Geocoder geocoder = new Geocoder(MainActivity.this,
                            Locale.getDefault());
                    // Initializing address list
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        // Setting locality
                        System.out.println(addresses.get(0).getLocality());
                        editTextLocation.setText(addresses.get(0).getLocality()
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // If location is null, opens settings.
                // Open Google Maps and try again
                else {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }

    public void savePrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String location = ((TextView) findViewById(R.id.editTextLocation)).getText().toString().trim();
        String days = ((TextView) findViewById(R.id.editTextDays)).getText().toString().trim();
        int rain = dropdown.getSelectedItemPosition();

        editor.putString(LOCATION, location);
        editor.putString(DAYS, days);
        editor.putInt(RAIN, rain);

        editor.apply();

        Toast.makeText(this, "Preferences Saved!", Toast.LENGTH_SHORT).show();
    }

    public void loadPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        location = ((TextView) findViewById(R.id.editTextLocation)).getText().toString();
        location = sharedPreferences.getString(LOCATION, "");
        days = sharedPreferences.getString(DAYS, "");
        rain = sharedPreferences.getInt(RAIN, -1);
    }

    public void updatePrefs() {
        editTextLocation.setText(location);
        editTextDays.setText(days);
        dropdown.setSelection(rain);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLocation:
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // If permission granted
                    getLocation();
                } else {
                    // If permission denied
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
                break;
            case R.id.buttonAlarm:
                Intent alarmIntent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(alarmIntent);
                break;
            case R.id.buttonForecast:
                if (checkFields()) {
                    Intent forecastIntent = new Intent(MainActivity.this, ForecastActivity.class);

                    String location = ((TextView) findViewById(R.id.editTextLocation)).getText().toString();
                    String days = ((TextView) findViewById(R.id.editTextDays)).getText().toString();
                    String rain = dropdown.getSelectedItem().toString();

                    forecastIntent.putExtra("location", location);
                    forecastIntent.putExtra("days", days);
                    forecastIntent.putExtra("rain", rain);
                    startActivity(forecastIntent);
                }
                break;
            case R.id.buttonPreferences:
                // Check fields for conditions before saving preferences
                if (checkFields()) {
                    savePrefs();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkFields() {
        location = ((TextView) findViewById(R.id.editTextLocation)).getText().toString().trim();
        days = ((TextView) findViewById(R.id.editTextDays)).getText().toString().trim();

        // Check fields for conditions before saving preferences
        if (location.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Location field is empty!", Toast.LENGTH_SHORT).show();
        } else if (days.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Days field is empty!", Toast.LENGTH_SHORT).show();
        } else if (!days.matches("1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16")) {
            Toast.makeText(getApplicationContext(), "Enter day(s) between 1 - 16", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    public static String returnLocation() {
        return location;
    }
}