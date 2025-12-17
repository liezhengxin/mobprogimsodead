package com.example.aolmobprog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import java.util.HashMap;
import java.util.Map;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddTodoActivity extends AppCompatActivity {

    EditText etTitle, etDesc;
    TextView tvSelectedLocation;
    Button btnSelectLocation, btnSave, btnBack;

    // Variabel untuk menyimpan koordinat
    private Double selectedLatitude = null;
    private Double selectedLongitude = null;
    private FirebaseFirestore db;

    // Cara modern untuk menangani hasil dari Activity lain
    private final ActivityResultLauncher<Intent> mapResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Cek apakah hasilnya OK dan ada data yang dikembalikan
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    selectedLatitude = data.getDoubleExtra("EXTRA_LATITUDE", 0.0);
                    selectedLongitude = data.getDoubleExtra("EXTRA_LONGITUDE", 0.0);

                    // Tampilkan lokasi yang dipilih di TextView
                    String locationText = String.format("Lat: %.4f, Lon: %.4f", selectedLatitude, selectedLongitude);
                    tvSelectedLocation.setText(locationText);
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        db = FirebaseFirestore.getInstance();

        // Hubungkan view dari layout
        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);


        btnSelectLocation.setOnClickListener(v -> {

            Intent mapIntent = new Intent(AddTodoActivity.this, MapActivity.class);

            mapResultLauncher.launch(mapIntent);
        });

        btnSave.setOnClickListener(v -> {
            saveTask();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDesc.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> todo = new HashMap<>();
        todo.put("title", title);
        todo.put("description", description);

        // Hanya tambahkan lokasi jika sudah dipilih
        if (selectedLatitude != null && selectedLongitude != null) {
            // Firestore punya tipe data khusus untuk lokasi geografis
            todo.put("location", new GeoPoint(selectedLatitude, selectedLongitude));
        } else {
            todo.put("location", null);
        }

        db.collection("todos")
                .add(todo)
                .addOnSuccessListener(documentReference -> {

                    runOnUiThread(() -> {
                        Toast.makeText(AddTodoActivity.this, "Task added successfully!", Toast.LENGTH_SHORT).show();

                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        Toast.makeText(AddTodoActivity.this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
        finish();
    }
}
