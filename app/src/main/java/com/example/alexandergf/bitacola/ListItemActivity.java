package com.example.alexandergf.bitacola;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ListItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    //model
    List<BiTacolaItem> items;
    //referencies pantalla
    private RecyclerView items_view;
    private Adapter adapter;
    private String folderId;



    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        Intent intent =getIntent();
        folderId = intent.getStringExtra("folderId");
        FloatingActionButton addFloatingBtn = findViewById(R.id.addFloatingBtn);

        items=new ArrayList<>();

        items_view = findViewById(R.id.items_view);
        adapter = new Adapter();

        items_view.setLayoutManager(new LinearLayoutManager(this));
        items_view.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        items_view.setAdapter(adapter);

        db.collection("Folders").document(folderId).collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                items.clear();
                for (DocumentSnapshot doc : documentSnapshots) {
                    String title = doc.getString("title");
                    String id = doc.getId();
                    final String[] fecha = new String[1];
                    if (doc.getDate("date") != null) {
                         fecha[0] = fmt.format(doc.getDate("date"));
                    }else{
                        fecha[0]="No disponible";
                    }
                    String autor = doc.getString("autor");
                    items.add(new BiTacolaItem(title,id, autor, fecha[0]));
                }
                adapter.notifyDataSetChanged();
            }
        });

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
        intent.putExtra("itemId",item.getId());
        intent.putExtra("folderId", folderId);
        startActivity(intent);
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView item_name;
        private TextView autorView;
        private TextView dataView;
        private ImageView fotoView;
        private ImageView buttonPlus;
        public ItemHolder(@NonNull final View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            autorView = itemView.findViewById(R.id.autorView);
            dataView = itemView.findViewById(R.id.dataView);
            fotoView = itemView.findViewById(R.id.fotoView);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);

            buttonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ListItemActivity.this, buttonPlus);
                    popupMenu.getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
                    BiTacolaItem item = items.get(getAdapterPosition());
                    final String id = item.getId();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_editar:
                                    goToEditItem(id);
                                    break;
                                case R.id.menu_esborrar:
                                    deleteItem(id,getAdapterPosition());
                                    break;
                                case android.R.id.home:
                                    onBackPressed();
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(getAdapterPosition());
                }
            });
        }
        public void bind(BiTacolaItem item) {
            item_name.setText(item.getName());
            dataView.setText(item.getFecha());
            String autor="85XLcnC1hVZnjOwQrhLd";
            if (item.getAutor() != null){
                autor=item.getAutor();
            }
            db.collection("Users").document(autor).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                   autorView.setText(documentSnapshot.getString("name"));
                }
            });
            StorageReference imgRef = storageReference.child("test/"+ item.getId() );
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null){
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(fotoView);
                    }


                }
            });
        }
    }
    private void deleteItem(final String id,final int pos) {
        AlertDialog.Builder builder =new AlertDialog.Builder(ListItemActivity.this);
        builder.setMessage("Estàs segur de que vols esborrar aquest ítem?").setTitle("Borrar ítem");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                items.remove(pos);
                db.collection("Folders").document(folderId).collection("Items").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ListItemActivity.this, "Ítem esborrat correctament.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListItemActivity.this, "Error al borrar l'ítem.", Toast.LENGTH_SHORT).show();
                    }
                });

                StorageReference filepath = storageReference.child(folderId).child(id);
                filepath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void goToEditItem(String id) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
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
