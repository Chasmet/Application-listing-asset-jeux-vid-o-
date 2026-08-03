package com.chk.assetlisting.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chk.assetlisting.R;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Region;
import com.chk.assetlisting.util.RegionListParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BulkRegionActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_SOURCE_REGION_ID = "source_region_id";

    private static final String[] TYPES = {
            "Région", "Île", "Village", "Ville", "Donjon", "Niveau",
            "Zone maritime", "Bâtiment", "Équipage", "Autre"
    };

    private DatabaseHelper database;
    private long gameId;
    private long sourceRegionId;
    private TextInputEditText editList;
    private TextView textDetected;
    private Spinner spinnerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_regions);

        database = new DatabaseHelper(this);
        gameId = getIntent().getLongExtra(EXTRA_GAME_ID, 0);
        sourceRegionId = getIntent().getLongExtra(EXTRA_SOURCE_REGION_ID, 0);
        if (gameId == 0) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        editList = findViewById(R.id.editRegionList);
        textDetected = findViewById(R.id.textDetectedCount);
        spinnerType = findViewById(R.id.spinnerType);
        MaterialButton save = findViewById(R.id.buttonCreateRegions);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(sourceRegionId == 0 ? "Ajouter une liste" : "Corriger la liste");
        save.setText(sourceRegionId == 0 ? "Créer les régions" : "Découper et remplacer");

        editList.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateDetectedCount(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        save.setOnClickListener(v -> saveRegions());

        if (sourceRegionId != 0) loadSourceRegion();
        updateDetectedCount();
    }

    private void loadSourceRegion() {
        Region source = database.getRegion(sourceRegionId);
        if (source == null || source.getGameId() != gameId) {
            sourceRegionId = 0;
            return;
        }
        editList.setText(source.getName());
        for (int i = 0; i < TYPES.length; i++) {
            if (TYPES[i].equalsIgnoreCase(source.getType())) {
                spinnerType.setSelection(i);
                break;
            }
        }
    }

    private void updateDetectedCount() {
        List<String> names = RegionListParser.parse(value(editList));
        if (names.isEmpty()) {
            textDetected.setText("Aucune région détectée");
        } else if (names.size() == 1) {
            textDetected.setText("1 région détectée");
        } else {
            textDetected.setText(names.size() + " régions détectées");
        }
    }

    private void saveRegions() {
        List<String> names = RegionListParser.parse(value(editList));
        if (names.isEmpty()) {
            editList.setError("Écris au moins une région");
            editList.requestFocus();
            return;
        }

        if (sourceRegionId != 0) {
            Region source = database.getRegion(sourceRegionId);
            if (source != null && source.getAssetCount() > 0) {
                Toast.makeText(this,
                        "Impossible de découper : cette région contient déjà des assets.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        Set<String> existing = new HashSet<>();
        for (Region region : database.getRegions(gameId)) {
            if (region.getId() != sourceRegionId) {
                existing.add(RegionListParser.comparisonKey(region.getName()));
            }
        }

        int added = 0;
        int skipped = 0;
        String type = String.valueOf(spinnerType.getSelectedItem());

        for (String name : names) {
            String key = RegionListParser.comparisonKey(name);
            if (TextUtils.isEmpty(key) || !existing.add(key)) {
                skipped++;
                continue;
            }

            Region region = new Region();
            region.setGameId(gameId);
            region.setName(name);
            region.setType(type);
            region.setImageUri("");
            region.setNotes("");
            database.saveRegion(region);
            added++;
        }

        if (sourceRegionId != 0 && added > 0) database.deleteRegion(sourceRegionId);

        if (added == 0) {
            Toast.makeText(this, "Toutes les régions existent déjà.", Toast.LENGTH_LONG).show();
            return;
        }

        String message = added + (added == 1 ? " région créée" : " régions créées");
        if (skipped > 0) message += " · " + skipped + " doublon(s) ignoré(s)";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private String value(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
