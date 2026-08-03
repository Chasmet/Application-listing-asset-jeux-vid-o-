package com.chk.assetlisting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chk.assetlisting.R;
import com.chk.assetlisting.adapter.AssetAdapter;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Asset;
import com.chk.assetlisting.model.Region;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RegionAssetsActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_REGION_ID = "region_id";

    private DatabaseHelper database;
    private long gameId;
    private long regionId;
    private AssetAdapter adapter;
    private TextView emptyText;
    private TextView countText;

    private final ActivityResultLauncher<Intent> editorLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> loadAssets());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_assets);

        database = new DatabaseHelper(this);
        gameId = getIntent().getLongExtra(EXTRA_GAME_ID, 0);
        regionId = getIntent().getLongExtra(EXTRA_REGION_ID, 0);

        if (gameId <= 0 || regionId <= 0) {
            Toast.makeText(this, "Région introuvable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Region region = database.getRegion(regionId);
        if (region == null || region.getGameId() != gameId) {
            Toast.makeText(this, "Cette région n’existe plus", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recycler = findViewById(R.id.recyclerAssets);
        emptyText = findViewById(R.id.textEmpty);
        countText = findViewById(R.id.textCount);
        FloatingActionButton add = findViewById(R.id.fabAddAsset);

        toolbar.setTitle(region.getName());
        toolbar.setSubtitle("Assets de la région");
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AssetAdapter(new AssetAdapter.Listener() {
            @Override
            public void onClick(Asset asset) {
                openAssetEditor(asset.getId());
            }

            @Override
            public void onLongClick(Asset asset) {
                showAssetActions(asset);
            }

            @Override
            public void onValidate(Asset asset) {
                int next = asset.getStatus() == Asset.STATUS_INTEGRATED
                        ? Asset.STATUS_CREATED : Asset.STATUS_INTEGRATED;
                database.updateAssetStatus(asset.getId(), next);
                Toast.makeText(RegionAssetsActivity.this,
                        next == Asset.STATUS_INTEGRATED
                                ? "Asset intégré ✓" : "Validation retirée",
                        Toast.LENGTH_SHORT).show();
                loadAssets();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        add.setOnClickListener(v -> openAssetEditor(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssets();
    }

    private void loadAssets() {
        if (database == null || adapter == null) return;
        List<Asset> assets = database.getAssets(gameId, regionId, "", -1);
        adapter.submitList(assets);
        countText.setText(assets.size() + (assets.size() > 1 ? " assets" : " asset"));
        emptyText.setVisibility(assets.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openAssetEditor(long assetId) {
        Intent intent = new Intent(this, EditAssetActivity.class);
        intent.putExtra(EditAssetActivity.EXTRA_GAME_ID, gameId);
        intent.putExtra(EditAssetActivity.EXTRA_REGION_ID, regionId);
        if (assetId > 0) intent.putExtra(EditAssetActivity.EXTRA_ASSET_ID, assetId);
        editorLauncher.launch(intent);
    }

    private void showAssetActions(Asset asset) {
        String[] actions = {
                "Modifier",
                "Marquer comme créé",
                "Valider dans le jeu",
                "Supprimer"
        };

        new AlertDialog.Builder(this)
                .setTitle(asset.getName())
                .setItems(actions, (dialog, which) -> {
                    if (which == 0) {
                        openAssetEditor(asset.getId());
                    } else if (which == 1) {
                        database.updateAssetStatus(asset.getId(), Asset.STATUS_CREATED);
                        loadAssets();
                    } else if (which == 2) {
                        database.updateAssetStatus(asset.getId(), Asset.STATUS_INTEGRATED);
                        loadAssets();
                    } else {
                        confirmDelete(asset);
                    }
                })
                .show();
    }

    private void confirmDelete(Asset asset) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l’asset ?")
                .setMessage(asset.getName() + " sera supprimé de la liste.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    database.deleteAsset(asset.getId());
                    loadAssets();
                })
                .show();
    }
}
