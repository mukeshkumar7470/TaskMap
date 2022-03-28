package com.mukeshkpdeveloper.taskmap.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

    //for bottomSheet
    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private ImageView header_Arrow_Image;
    private CoordinatorLayout bottom_cordi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        AppCompatButton btnUsers = findViewById(R.id.btn_user);

        bottom_cordi = findViewById(R.id.bottom_cordi);
        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);

        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow);

        header_Arrow_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                header_Arrow_Image.setRotation(slideOffset * 180);
            }
        });


        viewpoint();
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnUsers.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UsersActivity.class)));
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
                        }
                        addMarker(location);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(MainActivity.this, "Mukesh " + jObjError, Toast.LENGTH_SHORT).show();
                        }
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

    private void addMarker(ArrayList<Location> location) {


        for (int i = 0; i < location.size(); i++) {
            latLng = new LatLng(location.get(i).getLat(), location.get(i).get_long());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Viewport")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_fill))
                    .anchor(0.5f, 1)
                    .snippet("Lat: " + this.location.get(i).getLat() + "\nLong: " + this.location.get(i).get_long()));
        }
        /*From bitmap*/
        //.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.user_fill)))

        Log.d(TAG, "onResponse: " + location);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(location.get(0).getLat(), location.get(0).get_long()))
                .zoom(5)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mGoogleMap.animateCamera(camUpd3);
        mGoogleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.d(TAG, "onMapClick: "+marker.getPosition());
                bottom_cordi.setVisibility(View.VISIBLE);
                TextView view_point_name = findViewById(R.id.view_point_name);
                view_point_name.setText(""+marker.getPosition());
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                bottom_cordi.setVisibility(View.GONE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        /*mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Log.d(TAG, "onMapClick: "+marker.getPosition());
                bottom_cordi.setVisibility(View.VISIBLE);
                TextView view_point_name = findViewById(R.id.view_point_name);
                view_point_name.setText(""+marker.getPosition());
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });*/
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}