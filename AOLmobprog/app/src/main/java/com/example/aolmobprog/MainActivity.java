package com.example.aolmobprog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static final String TAG = "MainActivity"; // Tag untuk logging

    RecyclerView rvTodo;
    Button btnAdd, btnMap, btnDeleteAll;

    ArrayList<TodoContent> todoContentList;
    private TodoAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        rvTodo = findViewById(R.id.rvTodo);
        btnAdd = findViewById(R.id.btnAdd);
        btnMap = findViewById(R.id.btnMap);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        setupRecyclerView();

        listenForTodoUpdates();

        setupButtonListeners();
    }

    private void setupRecyclerView() {
        todoContentList = new ArrayList<>();
        adapter = new TodoAdapter(this, todoContentList, this);
        rvTodo.setAdapter(adapter);
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
    }

    private void listenForTodoUpdates() {
        db.collection("todos")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        todoContentList.clear();

                        for (QueryDocumentSnapshot doc : snapshots) {
                            TodoContent todo = doc.toObject(TodoContent.class);
                            todoContentList.add(todo);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Data updated. Total items: " + todoContentList.size());
                    }
                });
    }

    private void setupButtonListeners() {
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTodoActivity.class));
        });

        btnMap.setOnClickListener(v -> {
            // TODO: Tentukan fungsionalitas tombol ini. Mungkin menampilkan semua todo di peta?
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });

        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete all tasks? This action cannot be undone.")
                    .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                        deleteAllTasks();
                    })
                    .setNegativeButton("Cancel", null) // Tombol "Cancel" tidak melakukan apa-apa
                    .show();
        });
    }

    private void deleteAllTasks() {
        db.collection("todos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "All tasks successfully deleted.");
                        Toast.makeText(MainActivity.this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Error deleting tasks", e);
                        Toast.makeText(MainActivity.this, "Error deleting tasks", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting documents to delete", e);
                    Toast.makeText(MainActivity.this, "Could not fetch tasks to delete", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(int position) {
        if (position < 0 || position >= todoContentList.size()) {
            Log.e("MainActivity", "Invalid position clicked: " + position);
            return;
        }

        TodoContent clickedTodo = todoContentList.get(position);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("EXTRA_TITLE", clickedTodo.getTitle());
        intent.putExtra("EXTRA_DESCRIPTION", clickedTodo.getDescription());

        if (clickedTodo.getLatitude() != null && clickedTodo.getLongitude() != null) {
            intent.putExtra("EXTRA_LATITUDE", clickedTodo.getLatitude());
            intent.putExtra("EXTRA_LONGITUDE", clickedTodo.getLongitude());
            Log.d("MainActivity", "Sending location data: " + clickedTodo.getLatitude() + ", " + clickedTodo.getLongitude());
        } else {
            Log.d("MainActivity", "No location data to send for this item.");
        }


        startActivity(intent);
    }
}
