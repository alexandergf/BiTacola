package com.example.alexandergf.bitacola;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView hola;
    //model
    List<BiTacolaItem> items;
    //referencies pantalla
    private RecyclerView items_view;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);


        items=new ArrayList<>();
        items.add(new BiTacolaItem("Potatoes"));
        items.add(new BiTacolaItem("Toilet Paper"));

        items_view = findViewById(R.id.items_view);


        adapter = new Adapter();

        items_view.setLayoutManager(new LinearLayoutManager(this));
        items_view.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        items_view.setAdapter(adapter);
        /*hola = findViewById(R.id.hola);

        db.collection("Bitacola").document("bitacola").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                String code = documentSnapshot.getString("code");
                hola.setText(code);
            }
        });

        Map<String, Object> campos = new HashMap<>();
        campos.put("title", "escarabajo");
        campos.put("fecha", new Date());
        campos.put("icona", 123);
        db.collection("Bitacola").document("bitacola").collection("Items").add(campos).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ListItemActivity.this, "Gravat!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListItemActivity.this, "Ha fallat!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void onClickItem(int pos) {
        Toast.makeText(this, "Ave maria", Toast.LENGTH_SHORT).show();
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
