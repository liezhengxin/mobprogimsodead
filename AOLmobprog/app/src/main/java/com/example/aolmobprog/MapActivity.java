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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map); // Pastikan nama layout ini benar

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // Pastikan ID ini ada di activity_map.xml
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Atur posisi awal kamera (contoh: Jakarta)
        LatLng jakarta = new LatLng(-6.2088, 106.8456);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 10));

        // --- INI BAGIAN PENTINGNYA ---
        // Atur listener untuk klik di peta
        mMap.setOnMapClickListener(latLng -> {
            // Buat Intent untuk mengembalikan data
            Intent resultIntent = new Intent();
            // Masukkan data latitude dan longitude ke dalam intent
            resultIntent.putExtra("EXTRA_LATITUDE", latLng.latitude);
            resultIntent.putExtra("EXTRA_LONGITUDE", latLng.longitude);

            // Set hasilnya sebagai OK dan kirim intent kembali
            setResult(RESULT_OK, resultIntent);

            // Tutup MapActivity
            finish();
        });
    }
}
