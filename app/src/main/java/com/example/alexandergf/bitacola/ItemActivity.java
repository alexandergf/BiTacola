package com.example.alexandergf.bitacola;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.List;
import java.util.Locale;

import static java.lang.String.valueOf;

public class ItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String id;
    private String folder;
    private String lat;
    private String lon;

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
        id = intent.getStringExtra("itemId");
        folder = intent.getStringExtra("folderId");
        db.collection("Folders").document(folder).collection("Items").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                title_view.setText(documentSnapshot.getString("title"));
                db.collection("Users").document(documentSnapshot.getString("autor")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        autor_view.setText(documentSnapshot.getString("name"));
                    }
                });
                lat = valueOf(documentSnapshot.getGeoPoint("location").getLatitude());
                lon = valueOf(documentSnapshot.getGeoPoint("location").getLongitude());
                location_view.setText(getAddress(documentSnapshot.getGeoPoint("location").getLatitude(),documentSnapshot.getGeoPoint("location").getLongitude()));
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                date_view.setText(fmt.format(documentSnapshot.getDate("date")));
                description_view.setText(documentSnapshot.getString("desc"));
            }
        });

        StorageReference imgRef = storageReference.child("test/"+ id );

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        });

        location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?z=18&q="+lat+","+lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditItemActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_editar:
                goToEditItem();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void goToEditItem() {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("itemId", id);
        intent.putExtra("folderId",folder);
        startActivity(intent);
    }

    private String getAddress(double lat,double lng) {

        Geocoder geocoder = new Geocoder(ItemActivity.this, Locale.getDefault());
        String address="";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj;
            if (addresses.isEmpty() == false) {
                obj = addresses.get(0);
                String  add = obj.getLocality();
                add = add +", "+ obj.getCountryName();
                address=add;
            } else {
                address="No es troba la direccio afegida";
            }

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }
}
