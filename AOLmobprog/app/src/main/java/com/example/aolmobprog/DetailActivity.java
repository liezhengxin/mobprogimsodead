package com.example.aolmobprog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    private TextView tvTitle, tvDescription, tvLocationLabel;
    private Button btnBack;

    private Double latitude, longitude;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.detailTitle);
        tvDescription = findViewById(R.id.detailDescription);
        tvLocationLabel = findViewById(R.id.tvLocationLabel);
        mapView = findViewById(R.id.detailMapView);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });


        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String description = getIntent().getStringExtra("EXTRA_DESCRIPTION");

        if (getIntent().hasExtra("EXTRA_LATITUDE")) {
            latitude = getIntent().getDoubleExtra("EXTRA_LATITUDE", 0);
            longitude = getIntent().getDoubleExtra("EXTRA_LONGITUDE", 0);
        }

        tvTitle.setText(title);
        tvDescription.setText(description);

        if (latitude != null && longitude != null) {
            tvLocationLabel.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.VISIBLE);

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setAllGesturesEnabled(false);

        LatLng location = new LatLng(latitude, longitude);

        gMap.addMarker(new MarkerOptions().position(location).title("Task Location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        if (mapView != null) {
            mapView.onSaveInstanceState(mapViewBundle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    protected void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) mapView.onDestroy();
        super.onDestroy();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
