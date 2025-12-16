package com.example.aolmobprog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvTodo;
    Button btnAdd, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTodo = findViewById(R.id.rvTodo);
        btnAdd = findViewById(R.id.btnAdd);
        btnMap = findViewById(R.id.btnMap);

        // ============================
        // TODO (TUGAS KLEAN Lengkapin ini aja):
        // 1. Set LayoutManager (LinearLayoutManager)
        // 2. Create RecyclerView Adapter
        // 3. Fetch data from Firebase Firestore
        // 4. Bind data to RecyclerView
        // ============================

        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTodoActivity.class));
        });

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });
    }
}