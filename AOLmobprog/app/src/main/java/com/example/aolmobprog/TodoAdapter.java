package com.example.aolmobprog;import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<TodoContent> todoContentList;

    public TodoAdapter(Context context, ArrayList<TodoContent> todoContentList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.todoContentList = todoContentList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Membuat view dari layout item_todo.xml
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        // Menetapkan data ke view
        holder.tvTitle.setText(todoContentList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah item dalam list
        return todoContentList.size();
    }

    // ViewHolder: Menyimpan referensi view untuk satu item
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public TodoViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle); // Pastikan ID ini ada di item_todo.xml

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
