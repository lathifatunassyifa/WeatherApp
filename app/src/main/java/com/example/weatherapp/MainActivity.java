package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.helper.WeatherCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements com.android.volley.Response.Listener<JSONObject>, com.android.volley.Response.ErrorListener {
    private final String TAG = "MainActivity";
    private RequestQueue requestQueue;
    private ActivityMainBinding binding;
    private WeatherAdapter weatherAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.requestQueue = Volley.newRequestQueue(this);

        getDataFromVolley();
    }



    private void getDataFromVolley() {
        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.open-meteo.com/v1/forecast?latitude=-7.98&longitude=112.63&daily=weathercode&current_weather=true&timezone=auto",
                null,
                this,
                this
        );
        this.requestQueue.add(jr);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject crnWeather = response.getJSONObject("current_weather");
            int weatherCode = Integer.parseInt(crnWeather.getString("weathercode"));

            JSONObject daily = response.getJSONObject("daily");
            JSONArray time = daily.getJSONArray("time");
            JSONArray wCode = daily.getJSONArray("weathercode");

            List<String> timeList = new ArrayList<String>();
            List<Integer> wCodeList = new ArrayList<Integer>();

            for (int i = 0; i < time.length(); i++) {
                timeList.add(time.getString(i));
                wCodeList.add(Integer.parseInt(wCode.getString(i)));
            }

            String latitude = response.getString("latitude");
            String longitude = response.getString("longitude");

            binding.ivCuaca.setImageResource(WeatherCode.getCodeIcon(weatherCode));
            binding.tvTemperature.setText(crnWeather.getString("temperature") + "°C");
            binding.tvKondisi.setText(WeatherCode.getKondisi(Integer.parseInt(crnWeather.getString("weathercode"))));
            binding.tvWindSpeed.setText("Windspeed : " + crnWeather.getString("windspeed"));
            binding.tvKoordinat.setText("Koordinat : " + latitude + ", " + longitude);

            weatherAdapter = new WeatherAdapter(timeList, wCodeList, timeList.size());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            binding.rvWeathers.setLayoutManager(layoutManager);
            binding.rvWeathers.setHasFixedSize(true);
            binding.rvWeathers.setAdapter(weatherAdapter);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}