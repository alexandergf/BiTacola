package com.example.alexandergf.bitacola;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //model
    List<BiTacolaItem> items;
    //referencies pantalla
    private RecyclerView items_view;
    private Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        FloatingActionButton addFloatingBtn = findViewById(R.id.addFloatingBtn);

        items=new ArrayList<>();

        items_view = findViewById(R.id.items_view);

        db.collection("Folders").document("test").collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                items.clear();
                for (DocumentSnapshot doc : documentSnapshots) {
                    String title = doc.getString("title");
                    String id = doc.getId();
                    items.add(new BiTacolaItem(title,id));
                }
                adapter.notifyDataSetChanged();
            }
        });


        adapter = new Adapter();

        items_view.setLayoutManager(new LinearLayoutManager(this));
        items_view.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        items_view.setAdapter(adapter);


        addFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListItemActivity.this,EditItemActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickItem(int pos) {
        BiTacolaItem item = items.get(pos);
        Intent intent = new Intent(this,ItemActivity.class);
        intent.putExtra("id",item.getId());
        startActivity(intent);
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView item_name;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(getAdapterPosition());
                }
            });
        }
        public void bind(BiTacolaItem item) {
            item_name.setText(item.getName());

        }
    }

    public class Adapter extends RecyclerView.Adapter<ItemHolder>{

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.activity_list_item_view, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int pos) {
            holder.bind(items.get(pos));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
