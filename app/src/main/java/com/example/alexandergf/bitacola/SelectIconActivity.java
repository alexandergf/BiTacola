package com.example.alexandergf.bitacola;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

class SelectIconActivity extends AppCompatActivity {

    private List<String> icons = Arrays.asList(
            "ic_audiotrack_black_24dp", "ic_folder_black_24dp", "ic_local_florist_black_24dp", "ic_nature_people_black_24dp", "ic_restaurant_black_24dp", "ic_school_black_24dp", "ic_videogame_asset_black_24dp"
    );

    private RecyclerView iconList_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iconList_view = new RecyclerView(this);
        iconList_view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        setContentView(iconList_view);

        iconList_view.setLayoutManager(new GridLayoutManager(this, 3));
        iconList_view.setAdapter(new IconListAdapter());
    }

    void onIconSelect(int position) {
        String icon = icons.get(position);
        Intent data = new Intent();
        data.putExtra("icon", icon);
        setResult(RESULT_OK, data);
        finish();
    }

    private class IconViewHolder extends RecyclerView.ViewHolder{
        View icon_view;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            icon_view = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onIconSelect(getAdapterPosition());
                }
            });
        }
    }

    private class IconListAdapter extends RecyclerView.Adapter<IconViewHolder> {
        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int side = parent.getWidth() / 3;
            View view = new View(SelectIconActivity.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(side, side));
            return new IconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
            holder.icon_view.setBackground(Drawable.createFromPath(icons.get(position)));
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }
    }


}
