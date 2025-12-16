package com.example.aolmobprog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddTodoActivity extends AppCompatActivity {

    EditText etTitle, etDesc;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {

            String title = etTitle.getText().toString();
            String desc = etDesc.getText().toString();

            // ============================
            // TODO (Again lengkapin ini aja):
            // 1. Validate input
            // 2. Save data to Firebase Firestore
            // 3. Return to MainActivity
            // ============================

            finish(); // temporary
        });
    }
}