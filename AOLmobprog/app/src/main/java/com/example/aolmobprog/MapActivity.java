package com.example.aolmobprog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String MODE = "MODE";
    public static final String MODE_PICK = "PICK";
    public static final String MODE_VIEW = "VIEW";

    private String mode;
    private Double latitude, longitude;
    private String title, description;

    private GoogleMap mMap;

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mode = getIntent().getStringExtra(MODE);

        if (MODE_VIEW.equals(mode)) {
            latitude = getIntent().getDoubleExtra("EXTRA_LATITUDE", 0);
            longitude = getIntent().getDoubleExtra("EXTRA_LONGITUDE", 0);
            title = getIntent().getStringExtra("EXTRA_TITLE");
            description = getIntent().getStringExtra("EXTRA_DESCRIPTION");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng jakarta = new LatLng(-6.2088, 106.8456);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 10));

        if (MODE_PICK.equals(mode)) {
            setupPickMode();
        } else if (MODE_VIEW.equals(mode)) {
            setupViewMode();
        }
    }

    private void setupPickMode() {
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));

            Intent resultIntent = new Intent();
            resultIntent.putExtra("EXTRA_LATITUDE", latLng.latitude);
            resultIntent.putExtra("EXTRA_LONGITUDE", latLng.longitude);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupViewMode() {
        LatLng location = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));

        mMap.setOnMarkerClickListener(clickedMarker -> {
            Intent intent = new Intent(MapActivity.this, DetailActivity.class);
            intent.putExtra("EXTRA_TITLE", title);
            intent.putExtra("EXTRA_DESCRIPTION", description);
            intent.putExtra("EXTRA_LATITUDE", latitude);
            intent.putExtra("EXTRA_LONGITUDE", longitude);
            startActivity(intent);
            return true;
        });
    }
}
