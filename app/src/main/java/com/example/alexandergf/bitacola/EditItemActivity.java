package com.example.alexandergf.bitacola;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EditItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private FusedLocationProviderClient mFusedLocationClient;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int GALLERY_INTENT = 2;
    static final int MAP_INTENT = 3;
    private String id;
    public Uri image;
    public CharSequence placeName;
    Calendar mCurrentDate;

    LatLng latLng;
    GeoPoint point;
    Boolean flag;
    byte[] bitImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        final Button btn_cancel = findViewById(R.id.cancelButton);
        final Button btn_save = findViewById(R.id.saveButton);
        final ImageButton addImageButton = findViewById(R.id.addImageButton);
        final ImageButton selectImageButton = findViewById(R.id.selectImageButton);

        final EditText editTitle = findViewById(R.id.editTitle);
        final TextView editDate = findViewById(R.id.editDate);
        final EditText editDesc = findViewById(R.id.editDesc);
        final TextView locText = findViewById(R.id.locText);



        requestPermission();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(EditItemActivity.this);



        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentDate = Calendar.getInstance();
                int year = mCurrentDate.get(Calendar.YEAR);
                int month = mCurrentDate.get(Calendar.MONTH);
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(EditItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        selectedMonth++;
                        editDate.setText(selectedDay + "-" + selectedMonth + "-" + selectedYear);
                        mCurrentDate.set(selectedYear, selectedMonth, selectedDay);

                    }
                }, year, month, day);
                mDatePicker.show();
            }
        });

        locText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (ActivityCompat.checkSelfPermission(EditItemActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            point = new GeoPoint(location.getLatitude(),location.getLongitude());
                            latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            LatLngBounds latLngBounds = new LatLngBounds(latLng,latLng);

                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder().setLatLngBounds(latLngBounds);
                            try {
                                startActivityForResult(builder.build(EditItemActivity.this),MAP_INTENT);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTitle.getText() != null && editDesc.getText() != null && mCurrentDate != null && point != null && editDesc != null && (bitImg != null || image != null)){
                    final Map<String, Object> data = new HashMap<>();
                    data.put("title", editTitle.getText().toString());
                    data.put("desc", editDesc.getText().toString());
                    //TODO: completar user
                    data.put("autor", "pepito");
                    //-----------------------------
                    final Timestamp cal = new Timestamp(mCurrentDate.getTime());
                    db.collection("Folders").document("test").collection("Items")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String idInsert = documentReference.getId();
                                    db.collection("Folders").document("test").collection("Items").document(idInsert)
                                            .update("date", cal);
                                    db.collection("Folders").document("test").collection("Items").document(idInsert)
                                            .update("location", point);
                                    db.collection("Folders").document("test").collection("Items").document(idInsert)
                                            .update("photo", idInsert);
                                    if (flag == true) {
                                        StorageReference filepath = mStorage.child("test").child(idInsert);
                                        filepath.putBytes(bitImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                finish();
                                                //Toast.makeText(EditItemActivity.this, "Bieeeeen", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditItemActivity.this, "No se pudo subir la foto", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else if (flag == false) {
                                        StorageReference filepath = mStorage.child("test").child(idInsert);
                                        filepath.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                finish();
                                                //Toast.makeText(EditItemActivity.this, "Bieeeeen", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditItemActivity.this, "No se pudo subir la foto", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditItemActivity.this, "Fallo wey", Toast.LENGTH_SHORT).show();
                                }
                            });



                } else{
                    Toast.makeText(EditItemActivity.this, "Faltan camps per omplir.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
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

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        final ImageView imgView = findViewById(R.id.imgView);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            flag=true;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(imageBitmap);
            //Uri tempUri = getImageUri(getApplicationContext(),imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitImg = baos.toByteArray();


        }else if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            flag=false;
            Uri uri = data.getData();
            imgView.setImageURI(uri);
            image=uri;


        }else if (requestCode == MAP_INTENT && resultCode == RESULT_OK){
            final TextView locText = findViewById(R.id.locText);
            Place place = PlacePicker.getPlace(EditItemActivity.this,data);
            placeName=place.getName();
            locText.setText(placeName);
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},5);
    }
}
