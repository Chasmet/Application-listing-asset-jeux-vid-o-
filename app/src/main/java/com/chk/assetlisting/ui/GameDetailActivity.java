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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class GameDetailActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID="game_id";
    private DatabaseHelper database;private long gameId;private RegionAdapter adapter;private ImageView imageCover;private TextView gameName,description,progressText,emptyRegions;private ProgressBar progressBar;private MaterialToolbar toolbar;
    private final ActivityResultLauncher<Intent> editorLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->loadData());
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_game_detail);database=new DatabaseHelper(this);gameId=getIntent().getLongExtra(EXTRA_GAME_ID,0);if(gameId==0){finish();return;}toolbar=findViewById(R.id.toolbar);imageCover=findViewById(R.id.imageCover);gameName=findViewById(R.id.textGameName);description=findViewById(R.id.textDescription);progressText=findViewById(R.id.textProgress);progressBar=findViewById(R.id.progress);emptyRegions=findViewById(R.id.textEmptyRegions);RecyclerView recycler=findViewById(R.id.recyclerRegions);MaterialButton all=findViewById(R.id.buttonAllAssets);MaterialButton edit=findViewById(R.id.buttonEditGame);FloatingActionButton add=findViewById(R.id.fabAddRegion);toolbar.setNavigationOnClickListener(v->finish());adapter=new RegionAdapter(new RegionAdapter.Listener(){@Override public void onClick(Region r){Intent i=new Intent(GameDetailActivity.this,AssetListActivity.class);i.putExtra(AssetListActivity.EXTRA_GAME_ID,gameId);i.putExtra(AssetListActivity.EXTRA_REGION_ID,r.getId());startActivity(i);}@Override public void onLongClick(Region r){showRegionActions(r);}});recycler.setLayoutManager(new LinearLayoutManager(this));recycler.setAdapter(adapter);all.setOnClickListener(v->openAllAssets());edit.setOnClickListener(v->{Intent i=new Intent(this,EditGameActivity.class);i.putExtra(EditGameActivity.EXTRA_GAME_ID,gameId);editorLauncher.launch(i);});add.setOnClickListener(v->{Intent i=new Intent(this,EditRegionActivity.class);i.putExtra(EditRegionActivity.EXTRA_GAME_ID,gameId);editorLauncher.launch(i);});}
    @Override protected void onResume(){super.onResume();loadData();}
    private void loadData(){if(database==null)return;Game g=database.getGame(gameId);if(g==null){finish();return;}toolbar.setTitle(g.getName());gameName.setText(g.getName());description.setText(TextUtils.isEmpty(g.getDescription())?"Aucune description":g.getDescription());progressText.setText(g.getIntegratedCount()+" assets intégrés sur "+g.getAssetCount()+" · "+g.getProgressPercent()+"%");progressBar.setProgress(g.getProgressPercent());imageCover.setImageResource(R.drawable.ic_image);if(!TextUtils.isEmpty(g.getCoverUri()))try{imageCover.setImageURI(Uri.parse(g.getCoverUri()));}catch(Exception ignored){imageCover.setImageResource(R.drawable.ic_image);}List<Region> regions=database.getRegions(gameId);adapter.submitList(regions);emptyRegions.setVisibility(regions.isEmpty()?View.VISIBLE:View.GONE);}
    private void openAllAssets(){Intent i=new Intent(this,AssetListActivity.class);i.putExtra(AssetListActivity.EXTRA_GAME_ID,gameId);startActivity(i);}
    private void showRegionActions(Region r){String[] a={"Voir les assets","Modifier","Supprimer"};new AlertDialog.Builder(this).setTitle(r.getName()).setItems(a,(d,w)->{if(w==0){Intent i=new Intent(this,AssetListActivity.class);i.putExtra(AssetListActivity.EXTRA_GAME_ID,gameId);i.putExtra(AssetListActivity.EXTRA_REGION_ID,r.getId());startActivity(i);}else if(w==1){Intent i=new Intent(this,EditRegionActivity.class);i.putExtra(EditRegionActivity.EXTRA_GAME_ID,gameId);i.putExtra(EditRegionActivity.EXTRA_REGION_ID,r.getId());editorLauncher.launch(i);}else confirmDeleteRegion(r);}).show();}
    private void confirmDeleteRegion(Region r){new AlertDialog.Builder(this).setTitle("Supprimer la région ?").setMessage("Les assets seront conservés mais déplacés dans « Sans région ».").setNegativeButton("Annuler",null).setPositiveButton("Supprimer",(d,w)->{database.deleteRegion(r.getId());loadData();}).show();}
}
