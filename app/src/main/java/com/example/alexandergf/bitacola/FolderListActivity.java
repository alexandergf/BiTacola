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
import android.widget.Adapter;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        folders = new ArrayList<>();

        folders_view = findViewById(R.id.folders_view);
        addFloatingBtn = findViewById(R.id.addFloatingBtn);

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
    }

    public class FolderListItemHolder extends RecyclerView.ViewHolder{
        private TextView folder_name;

        public FolderListItemHolder(@NonNull View folderView) {
            super(folderView);
            folder_name = folderView.findViewById(R.id.folder_name);
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
        }
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
