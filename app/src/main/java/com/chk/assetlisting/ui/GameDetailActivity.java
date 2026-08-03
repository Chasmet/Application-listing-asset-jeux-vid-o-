package com.chk.assetlisting.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chk.assetlisting.R;
import com.chk.assetlisting.adapter.RegionAdapter;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Game;
import com.chk.assetlisting.model.Region;
import com.chk.assetlisting.util.RegionListParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GameDetailActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID = "game_id";

    private DatabaseHelper database;
    private long gameId;
    private RegionAdapter adapter;
    private ImageView imageCover;
    private TextView gameName;
    private TextView description;
    private TextView progressText;
    private TextView emptyRegions;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;
    private MaterialButton buttonAddList;
    private Region repairCandidate;

    private final ActivityResultLauncher<Intent> editorLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> loadData());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        database = new DatabaseHelper(this);
        gameId = getIntent().getLongExtra(EXTRA_GAME_ID, 0);
        if (gameId == 0) {
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        imageCover = findViewById(R.id.imageCover);
        gameName = findViewById(R.id.textGameName);
        description = findViewById(R.id.textDescription);
        progressText = findViewById(R.id.textProgress);
        progressBar = findViewById(R.id.progress);
        emptyRegions = findViewById(R.id.textEmptyRegions);
        buttonAddList = findViewById(R.id.buttonAddRegionList);
        RecyclerView recycler = findViewById(R.id.recyclerRegions);
        MaterialButton all = findViewById(R.id.buttonAllAssets);
        MaterialButton edit = findViewById(R.id.buttonEditGame);
        FloatingActionButton add = findViewById(R.id.fabAddRegion);

        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new RegionAdapter(new RegionAdapter.Listener() {
            @Override
            public void onClick(Region region) {
                openRegionAssets(region);
            }

            @Override
            public void onLongClick(Region region) {
                showRegionActions(region);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        all.setOnClickListener(v -> openAllAssets());
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditGameActivity.class);
            intent.putExtra(EditGameActivity.EXTRA_GAME_ID, gameId);
            editorLauncher.launch(intent);
        });
        add.setOnClickListener(v -> openSingleRegionEditor());
        buttonAddList.setOnClickListener(v -> openBulkRegions(repairCandidate));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (database == null) return;
        Game game = database.getGame(gameId);
        if (game == null) {
            finish();
            return;
        }

        toolbar.setTitle(game.getName());
        gameName.setText(game.getName());
        description.setText(TextUtils.isEmpty(game.getDescription())
                ? "Aucune description" : game.getDescription());
        progressText.setText(game.getIntegratedCount() + " assets intégrés sur "
                + game.getAssetCount() + " · " + game.getProgressPercent() + "%");
        progressBar.setProgress(game.getProgressPercent());

        imageCover.setImageResource(R.drawable.ic_image);
        if (!TextUtils.isEmpty(game.getCoverUri())) {
            try {
                imageCover.setImageURI(Uri.parse(game.getCoverUri()));
            } catch (Exception ignored) {
                imageCover.setImageResource(R.drawable.ic_image);
            }
        }

        List<Region> regions = database.getRegions(gameId);
        adapter.submitList(regions);
        emptyRegions.setVisibility(regions.isEmpty() ? View.VISIBLE : View.GONE);

        repairCandidate = null;
        for (Region region : regions) {
            if (region.getAssetCount() == 0
                    && RegionListParser.parse(region.getName()).size() >= 2) {
                repairCandidate = region;
                break;
            }
        }

        if (repairCandidate == null) {
            buttonAddList.setText("COLLER UNE LISTE");
        } else {
            buttonAddList.setText("CORRIGER LA LISTE");
        }
    }

    private void openSingleRegionEditor() {
        Intent intent = new Intent(this, EditRegionActivity.class);
        intent.putExtra(EditRegionActivity.EXTRA_GAME_ID, gameId);
        editorLauncher.launch(intent);
    }

    private void openBulkRegions(Region source) {
        Intent intent = new Intent(this, BulkRegionActivity.class);
        intent.putExtra(BulkRegionActivity.EXTRA_GAME_ID, gameId);
        if (source != null) {
            intent.putExtra(BulkRegionActivity.EXTRA_SOURCE_REGION_ID, source.getId());
        }
        editorLauncher.launch(intent);
    }

    private void openAllAssets() {
        Intent intent = new Intent(this, AssetListActivity.class);
        intent.putExtra(AssetListActivity.EXTRA_GAME_ID, gameId);
        startActivity(intent);
    }

    private void openRegionAssets(Region region) {
        Intent intent = new Intent(this, AssetListActivity.class);
        intent.putExtra(AssetListActivity.EXTRA_GAME_ID, gameId);
        intent.putExtra(AssetListActivity.EXTRA_REGION_ID, region.getId());
        startActivity(intent);
    }

    private void showRegionActions(Region region) {
        List<String> actions = new ArrayList<>();
        actions.add("Voir les assets");
        actions.add("Modifier");
        if (region.getAssetCount() == 0
                && RegionListParser.parse(region.getName()).size() >= 2) {
            actions.add("Découper cette liste");
        }
        actions.add("Supprimer");

        new AlertDialog.Builder(this)
                .setTitle(region.getName())
                .setItems(actions.toArray(new String[0]), (dialog, which) -> {
                    String action = actions.get(which);
                    if ("Voir les assets".equals(action)) {
                        openRegionAssets(region);
                    } else if ("Modifier".equals(action)) {
                        Intent intent = new Intent(this, EditRegionActivity.class);
                        intent.putExtra(EditRegionActivity.EXTRA_GAME_ID, gameId);
                        intent.putExtra(EditRegionActivity.EXTRA_REGION_ID, region.getId());
                        editorLauncher.launch(intent);
                    } else if ("Découper cette liste".equals(action)) {
                        openBulkRegions(region);
                    } else if ("Supprimer".equals(action)) {
                        confirmDeleteRegion(region);
                    }
                })
                .show();
    }

    private void confirmDeleteRegion(Region region) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la région ?")
                .setMessage("Les assets seront conservés mais déplacés dans « Sans région ».")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    database.deleteRegion(region.getId());
                    loadData();
                })
                .show();
    }
}
