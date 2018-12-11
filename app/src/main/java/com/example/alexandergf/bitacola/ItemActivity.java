package com.example.alexandergf.bitacola;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import static java.lang.String.valueOf;

public class ItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


        final ImageView imageView = findViewById(R.id.imageView);
        final TextView title_view = findViewById(R.id.title_view);
        final TextView autor_view = findViewById(R.id.autor_view);
        final TextView location_view = findViewById(R.id.location_view);
        final TextView date_view = findViewById(R.id.date_view);
        final TextView description_view = findViewById(R.id.description_view);


        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        db.collection("Folders").document("test").collection("Items").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                //documentSnapshot.getId
                title_view.setText(documentSnapshot.getString("title"));
                autor_view.setText(documentSnapshot.getString("autor"));
                //location_view.setText(valueOf(documentSnapshot.getGeoPoint("location")));
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                date_view.setText(fmt.format(documentSnapshot.getDate("date")));
                description_view.setText(documentSnapshot.getString("desc"));
            }
        });

        StorageReference imgRef = storageReference.child("test/"+id+".jpg");

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ItemActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_editar:
                goToEditItem();
                break;
            case R.id.menu_esborrar:
                deleteItem();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteItem() {

    }

    private void goToEditItem() {

    }
}
