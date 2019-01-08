package com.example.alexandergf.bitacola;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class User extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final EditText userText = findViewById(R.id.userText);
    final Button newBtn = findViewById(R.id.newBtn);
    final Button userBtn = findViewById(R.id.userBtn);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, Object> data = new HashMap<>();
                data.put("name",userText.getText().toString());
                db.collection("Users").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent= new Intent(User.this,FolderListActivity.class);
                        intent.putExtra("user",userText.getText().toString());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        String name=null;

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.getString("name").equals(userText.getText().toString())){
                                name=doc.getId();
                                db.collection("Users").document(name).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        documentSnapshot.getData();
                                    }
                                });
                            }

                        }
                        if (name != null){
                            Intent intent= new Intent(User.this,FolderListActivity.class);
                            intent.putExtra("user",name);
                            startActivity(intent);
                        } else {
                            Toast.makeText(User.this, "Usuari no creat.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}
