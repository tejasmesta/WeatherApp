package com.example.android.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RelativeLayout homeRL;
    private TextView cityName;
    private TextInputEditText cityEditText;
    private ImageView black, searchIcon, weatherConditionIcon;
    private TextView temperature, condition;
    private RecyclerView recyclerView;
    private ArrayList<WeatherModalClass> weatherModalClassArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private final int PERMISSION_CODE = 1;
    private String UsercityName;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        homeRL = findViewById(R.id.RLHome);
        cityName = findViewById(R.id.city_name);
        cityEditText = findViewById(R.id.edit_text2);
        black = findViewById(R.id.black);
        searchIcon = findViewById(R.id.search_icon);
        weatherConditionIcon = findViewById(R.id.weather_condition_icon);
        temperature = findViewById(R.id.temperature);
        condition = findViewById(R.id.condition);
        recyclerView = findViewById(R.id.recycler);
        weatherModalClassArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalClassArrayList);
        recyclerView.setAdapter(weatherAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            UsercityName = getCityName(location);
            getWeatherInfo(UsercityName);
        }

        searchIcon.setOnClickListener(v -> {
            String city = cityEditText.getText().toString();
            if (city.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please Enter city Name", Toast.LENGTH_SHORT).show();
            } else {
                cityName.setText(city);
                getWeatherInfo(city);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please grant the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(Location location)
    {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        String cityNAME = "Not Found";
        Geocoder goc = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addy = goc.getFromLocation(latitude,longitude,5);
            for (Address adr: addy)
            {
                if(adr!=null)
                {
                    String city = adr.getLocality();
                    if(city!=null && !city.equals(""))
                    {
                        cityNAME = city;
                    }
                    else
                    {
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityNAME;
    }

    private void getWeatherInfo(String cityname)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=adc3166e2c254b86a86140613212010&q="+cityname+"&days=1&aqi=yes&alerts=yes";
        cityName.setText(cityname);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this::onResponse, error -> Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        homeRL.setVisibility(View.VISIBLE);
        weatherModalClassArrayList.clear();
        try {
            String temp = response.getJSONObject("current").getString("temp_c");
            temperature.setText(temp + "°C");

            int isDay = response.getJSONObject("current").getInt("is_day");
            if (isDay == 1) {
                Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Morning%2C_just_after_sunrise%2C_Namibia.jpg/1920px-Morning%2C_just_after_sunrise%2C_Namibia.jpg").into(black);
            } else {
                Picasso.get().load("https://www.thebmc.co.uk/Handlers/ArticleImageHandler.ashx?id=7467&index=0&w=605&h=434").into(black);
            }

            String conditionn = response.getJSONObject("current").getJSONObject("condition").getString("text");
            condition.setText(conditionn);

            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
            Picasso.get().load("http:".concat(conditionIcon)).into(weatherConditionIcon);

            JSONObject forecastObject = response.getJSONObject("forecast");
            JSONObject forecastArray = forecastObject.getJSONArray("forecastday").getJSONObject(0);
            JSONArray hourArray = forecastArray.getJSONArray("hour");

            for (int i = 0; i < hourArray.length(); i++) {
                JSONObject hourObj = hourArray.getJSONObject(i);
                String TIME = hourObj.getString("time");
                String TEMPERATURE = hourObj.getString("temp_c");
                String ICON = hourObj.getJSONObject("condition").getString("icon");
                String WIND = hourObj.getString("wind_kph");
                weatherModalClassArrayList.add(new WeatherModalClass(TIME, TEMPERATURE, ICON, WIND));
            }
            weatherAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}