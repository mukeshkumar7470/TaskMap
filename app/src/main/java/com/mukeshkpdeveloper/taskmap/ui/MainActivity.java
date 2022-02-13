package com.mukeshkpdeveloper.taskmap.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukeshkpdeveloper.taskmap.R;
import com.mukeshkpdeveloper.taskmap.models.Location;
import com.mukeshkpdeveloper.taskmap.networking.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<Location> location;
    private LatLng latLng;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        AppCompatButton btnUsers = findViewById(R.id.btn_user);


        viewpoint();
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void viewpoint() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApiInterface().getViewport();
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "Data: " + response.body());
                    location = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        JSONArray jsonArray = jsonObject.getJSONArray("location");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Location locationData = new Location();
                            JSONObject obj = jsonArray.getJSONObject(i);
                            double lat = Double.parseDouble(obj.getString("lat"));
                            double longn = Double.parseDouble(obj.getString("long"));
                            locationData.setLat(lat);
                            locationData.set_long(longn);

                            location.add(locationData);
                            latLng = new LatLng(location.get(i).getLat(), location.get(i).get_long());
                            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Viewport").snippet("Lat: " + location.get(i).getLat() + "\nLong: " + location.get(i).get_long()));
                        }
                        Log.d(TAG, "onResponse: " + location);
                        CameraPosition camPos = new CameraPosition.Builder()
                                .target(new LatLng(location.get(0).getLat(), location.get(0).get_long()))
                                .zoom(5)
                                .build();
                        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                        mGoogleMap.animateCamera(camUpd3);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        assert response.errorBody() != null;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, "Mukesh " + jObjError, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

}