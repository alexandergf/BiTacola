package com.example.alexandergf.bitacola;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class EditFolderActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String folderId;
    private EditText editTitle;
    private EditText editShare;
    private EditText editCode;
    private ImageButton iconBtn;
    private ImageButton shareBtn;
    private Button cancelBtn;
    private Button saveBtn;

    private Double icon = 2d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);

        getSupportActionBar().setTitle("Editar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = findViewById(R.id.editTitle);
        editShare = findViewById(R.id.editShare);
        editCode = findViewById(R.id.editCode);
        iconBtn = findViewById(R.id.iconBtn);
        shareBtn = findViewById(R.id.shareBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        folderId = intent.getStringExtra("folderId");

        if (folderId != null) {
            db.collection("Folders").document(folderId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    editTitle.setText(documentSnapshot.getString("title"));
                    icon = documentSnapshot.getDouble("icona");
                    if (1d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_audiotrack_black_24dp);
                    } else if (2d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_folder_black_24dp);
                    } else if (3d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_local_florist_black_24dp);
                    } else if (4d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_nature_people_black_24dp);
                    } else if (5d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_restaurant_black_24dp);
                    } else if (6d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_school_black_24dp);
                    } else if (7d == icon) {
                        iconBtn.setImageResource(R.drawable.ic_videogame_asset_black_24dp);
                    }
                    editCode.setText(documentSnapshot.getId());

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*TODO: crear codi QR i mostrar per pantalla*/
                        }
                    });
                }
            });
        } else {
            shareBtn.setClickable(false);
        }

        iconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectIcon = new Intent(EditFolderActivity.this, SelectIconActivity.class);
                startActivityForResult(selectIcon, 0);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTitle.getText() != null) {
                    final Map<String, Object> data = new HashMap<>();
                    data.put("title", editTitle.getText().toString());
                    data.put("icona", icon);
                    if (folderId != null) {
                        db.collection("Folders").document(folderId).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditFolderActivity.this, "Error en modificar les dades de la carpeta.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        db.collection("Folders").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditFolderActivity.this, "Error en crear la carpeta.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(EditFolderActivity.this, "Falten camps per omplir.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    iconBtn.setImageResource(Integer.parseInt(data.getStringExtra("icon")));
                    if (data.getStringExtra("icon") == "ic_audiotrack_black_24dp") {
                        icon = 1d;
                    } else if (data.getStringExtra("icon") == "ic_folder_black_24dp") {
                        icon = 2d;
                    } else if (data.getStringExtra("icon") == "ic_local_florist_black_24dp") {
                        icon = 3d;
                    }else if (data.getStringExtra("icon") == "ic_nature_people_black_24dp") {
                        icon = 4d;
                    }else if (data.getStringExtra("icon") == "ic_restaurant_black_24dp") {
                        icon = 5d;
                    }else if (data.getStringExtra("icon") == "ic_school_black_24dp") {
                        icon = 6d;
                    }else if (data.getStringExtra("icon") == "ic_videogame_black_24dp") {
                        icon = 7d;
                    }
                }
                break;
        }
    }
}
