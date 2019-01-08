package com.example.alexandergf.bitacola;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FolderListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<BiTacolaFolder> folders;

    private RecyclerView folders_view;
    private FolderListAdapter adapter;
    private FloatingActionButton addFloatingBtn;
    String nameUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);


        folders = new ArrayList<>();

        folders_view = findViewById(R.id.folders_view);
        addFloatingBtn = findViewById(R.id.addFloatingBtn);
        /*--------------------------------------------------Para los users
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("user");*/
        //db.collection("Users").document(nameUser).
        //--------------------------------------------------

        db.collection("Folders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                folders.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    String id = doc.getId();
                    String title = doc.getString("title");
                    Double icon = doc.getDouble("icona");
                    folders.add(new BiTacolaFolder(id,title,icon));
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new FolderListAdapter();

        folders_view.setLayoutManager(new LinearLayoutManager(this));
        folders_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        folders_view.setAdapter(adapter);

        addFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FolderListActivity.this, EditFolderActivity.class);
                startActivity(intent);
            }
        });
    }

    public class FolderListItemHolder extends RecyclerView.ViewHolder{
        private TextView folder_name;
        private ImageView iconView;
        private ImageView optionBtn;

        public FolderListItemHolder(@NonNull View folderView) {
            super(folderView);
            folder_name = folderView.findViewById(R.id.folder_name);
            iconView = folderView.findViewById(R.id.iconView);
            optionBtn = folderView.findViewById(R.id.optionBtn);

            optionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(FolderListActivity.this, optionBtn);
                    popupMenu.getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
                    BiTacolaFolder folder = folders.get(getAdapterPosition());
                    final String id =folder.getId();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.menu_editar:
                                    goToEditFolder(id);
                                    break;
                                case R.id.menu_esborrar:
                                    deleteFolder(id, getAdapterPosition());
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

            folderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BiTacolaFolder folder = folders.get(getAdapterPosition());
                    Intent intent = new Intent(FolderListActivity.this, ListItemActivity.class);
                    intent.putExtra("folderId", folder.getId());
                    startActivity(intent);
                }
            });
        }

        public void bind(BiTacolaFolder folder) {
            folder_name.setText(folder.getTitle());
            if (1d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_audiotrack_black_24dp));
            } else if (2d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder_black_24dp));
            } else if (3d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_local_florist_black_24dp));
            } else if (4d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_nature_people_black_24dp));
            } else if (5d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
            } else if (6d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_school_black_24dp));
            } else if (7d == folder.getIcon()) {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_videogame_asset_black_24dp));
            }
        }
    }

    private void deleteFolder(final String id, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FolderListActivity.this);
        builder.setMessage("Estàs segur de que vols esborrar aquesta carpeta?").setTitle("Borrar carpeta");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                folders.remove(position);
                db.collection("Folders").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FolderListActivity.this, "Carpeta esborrada correctament.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FolderListActivity.this, "Error al borrar la carpeta.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToEditFolder(String id) {
        Intent intent = new Intent(FolderListActivity.this, EditFolderActivity.class);
        intent.putExtra("folderId", id);
        startActivity(intent);
    }

    public class FolderListAdapter extends RecyclerView.Adapter<FolderListActivity.FolderListItemHolder>{

        @NonNull
        @Override
        public FolderListActivity.FolderListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View folderView = getLayoutInflater().inflate(R.layout.activity_folder_list_view, parent, false);
            return new FolderListActivity.FolderListItemHolder(folderView);
        }

        @Override
        public void onBindViewHolder(@NonNull FolderListActivity.FolderListItemHolder holder, int position) {
            holder.bind(folders.get(position));
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }
    }
}
