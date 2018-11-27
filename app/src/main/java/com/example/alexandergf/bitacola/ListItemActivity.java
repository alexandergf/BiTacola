package com.example.alexandergf.bitacola;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListItemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView hola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        hola = findViewById(R.id.hola);

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
        });
    }
}
