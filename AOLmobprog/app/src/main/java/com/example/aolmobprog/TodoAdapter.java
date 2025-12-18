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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.tvTitle.setText(todoContentList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return todoContentList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public TodoViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);

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
