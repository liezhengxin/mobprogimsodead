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

    // Deklarasi komponen UI
    RecyclerView rvTodo;
    Button btnAdd, btnMap, btnDeleteAll;

    // Deklarasi untuk data dan adapter
    ArrayList<TodoContent> todoContentList;
    private TodoAdapter adapter;

    // Deklarasi untuk Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();

        // 2. Hubungkan komponen UI dengan ID di layout
        rvTodo = findViewById(R.id.rvTodo);
        btnAdd = findViewById(R.id.btnAdd);
        btnMap = findViewById(R.id.btnMap);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        // 3. Siapkan RecyclerView dan Adapter
        setupRecyclerView();

        // 4. Panggil metode untuk mendengarkan data dari Firestore
        listenForTodoUpdates();

        // 5. Atur listener untuk tombol
        setupButtonListeners();
    }

    private void setupRecyclerView() {
        // Inisialisasi list data (awalnya kosong)
        todoContentList = new ArrayList<>();
        // Buat adapter dengan list yang masih kosong
        adapter = new TodoAdapter(this, todoContentList, this);
        // Hubungkan adapter ke RecyclerView
        rvTodo.setAdapter(adapter);
        // Atur layout manager (bagaimana item akan ditampilkan, misal: linear dari atas ke bawah)
        rvTodo.setLayoutManager(new LinearLayoutManager(this));
    }

    private void listenForTodoUpdates() {
        // Membuat query untuk mengambil data dari koleksi "todos"
        // Anda bisa menambahkan .orderBy() jika ingin mengurutkan data
        db.collection("todos")
                .addSnapshotListener((snapshots, e) -> {
                    // Cek jika ada error saat mengambil data
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    // Cek jika snapshot tidak null (ada data yang diterima)
                    if (snapshots != null) {
                        // Bersihkan list lama agar data tidak menumpuk (duplikat) setiap ada update
                        todoContentList.clear();

                        // Loop melalui setiap dokumen yang diterima dari Firestore
                        for (QueryDocumentSnapshot doc : snapshots) {
                            // Ubah setiap dokumen menjadi objek TodoContent
                            // Firestore akan otomatis mencocokkan field di database dengan nama variabel di class
                            TodoContent todo = doc.toObject(TodoContent.class);
                            // Tambahkan objek yang sudah jadi ke dalam list
                            todoContentList.add(todo);
                        }
                        // Beri tahu adapter bahwa seluruh dataset telah berubah, lalu refresh RecyclerView
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Data updated. Total items: " + todoContentList.size());
                    }
                });
    }

    private void setupButtonListeners() {
        // Listener untuk tombol "Add"
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTodoActivity.class));
        });

        // Listener untuk tombol "Map"
        btnMap.setOnClickListener(v -> {
            // TODO: Tentukan fungsionalitas tombol ini. Mungkin menampilkan semua todo di peta?
            // Untuk sekarang, bisa membuka MapActivity biasa.
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });

        btnDeleteAll.setOnClickListener(v -> {
            // Tampilkan dialog konfirmasi sebelum menghapus
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete all tasks? This action cannot be undone.")
                    .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                        // Jika pengguna menekan "Yes", panggil fungsi hapus
                        deleteAllTasks();
                    })
                    .setNegativeButton("Cancel", null) // Tombol "Cancel" tidak melakukan apa-apa
                    .show();
        });
    }

    private void deleteAllTasks() {
        // Menghapus semua dokumen dalam sebuah koleksi agak rumit, cara terbaik adalah
        // dengan mengambil semua dokumen lalu menghapusnya dalam satu batch.

        db.collection("todos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Buat "WriteBatch" - ini seperti satu transaksi besar
                    WriteBatch batch = db.batch();

                    // Loop melalui setiap dokumen yang ditemukan
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Tambahkan perintah hapus untuk setiap dokumen ke dalam batch
                        batch.delete(doc.getReference());
                    }

                    // Jalankan semua perintah hapus dalam batch
                    batch.commit().addOnSuccessListener(aVoid -> {
                        // Jika batch berhasil dijalankan
                        Log.d(TAG, "All tasks successfully deleted.");
                        Toast.makeText(MainActivity.this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                        // List akan otomatis kosong karena listener masih aktif
                    }).addOnFailureListener(e -> {
                        // Jika batch gagal
                        Log.w(TAG, "Error deleting tasks", e);
                        Toast.makeText(MainActivity.this, "Error deleting tasks", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting documents to delete", e);
                    Toast.makeText(MainActivity.this, "Could not fetch tasks to delete", Toast.LENGTH_SHORT).show();
                });
    }

    // Metode ini akan dipanggil dari Adapter saat sebuah item di-klik
    @Override
    public void onItemClick(int position) {
        // Pastikan posisi yang diklik valid untuk menghindari crash
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
