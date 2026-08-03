package com.chk.assetlisting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.chk.assetlisting.model.Game;
import com.chk.assetlisting.model.Region;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class AssetListActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID="game_id",EXTRA_REGION_ID="region_id";
    private DatabaseHelper database;private long gameId,regionId;private int statusFilter=-1;private AssetAdapter adapter;private TextInputEditText searchInput;private TextView emptyText,countText;
    private final ActivityResultLauncher<Intent> editorLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->loadAssets());
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_asset_list);database=new DatabaseHelper(this);gameId=getIntent().getLongExtra(EXTRA_GAME_ID,0);regionId=getIntent().getLongExtra(EXTRA_REGION_ID,0);if(gameId==0){finish();return;}MaterialToolbar toolbar=findViewById(R.id.toolbar);searchInput=findViewById(R.id.editSearch);emptyText=findViewById(R.id.textEmpty);countText=findViewById(R.id.textCount);ChipGroup chips=findViewById(R.id.chipGroup);RecyclerView recycler=findViewById(R.id.recyclerAssets);FloatingActionButton add=findViewById(R.id.fabAddAsset);toolbar.setNavigationOnClickListener(v->finish());Game g=database.getGame(gameId);if(regionId>0){Region r=database.getRegion(regionId);toolbar.setTitle(r==null?"Assets":r.getName());}else toolbar.setTitle(g==null?"Assets":g.getName()+" · Assets");adapter=new AssetAdapter(new AssetAdapter.Listener(){@Override public void onClick(Asset a){editAsset(a.getId());}@Override public void onLongClick(Asset a){showAssetActions(a);}@Override public void onValidate(Asset a){int next=a.getStatus()==Asset.STATUS_INTEGRATED?Asset.STATUS_CREATED:Asset.STATUS_INTEGRATED;database.updateAssetStatus(a.getId(),next);Toast.makeText(AssetListActivity.this,next==Asset.STATUS_INTEGRATED?"Asset validé ✓":"Validation retirée",Toast.LENGTH_SHORT).show();loadAssets();}});recycler.setLayoutManager(new LinearLayoutManager(this));recycler.setAdapter(adapter);searchInput.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){loadAssets();}public void afterTextChanged(Editable e){}});chips.setOnCheckedStateChangeListener((group,ids)->{if(ids.isEmpty())return;int checked=ids.get(0);if(checked==R.id.chipTodo)statusFilter=Asset.STATUS_TODO;else if(checked==R.id.chipCreated)statusFilter=Asset.STATUS_CREATED;else if(checked==R.id.chipIntegrated)statusFilter=Asset.STATUS_INTEGRATED;else statusFilter=-1;loadAssets();});add.setOnClickListener(v->editAsset(0));}
    @Override protected void onResume(){super.onResume();loadAssets();}
    private void loadAssets(){if(database==null||adapter==null||searchInput==null)return;String q=searchInput.getText()==null?"":searchInput.getText().toString();List<Asset> assets=database.getAssets(gameId,regionId,q,statusFilter);adapter.submitList(assets);countText.setText(assets.size()+(assets.size()>1?" assets":" asset"));emptyText.setVisibility(assets.isEmpty()?View.VISIBLE:View.GONE);}
    private void editAsset(long id){Intent i=new Intent(this,EditAssetActivity.class);i.putExtra(EditAssetActivity.EXTRA_GAME_ID,gameId);i.putExtra(EditAssetActivity.EXTRA_REGION_ID,regionId);if(id>0)i.putExtra(EditAssetActivity.EXTRA_ASSET_ID,id);editorLauncher.launch(i);}
    private void showAssetActions(Asset a){String[] actions={"Modifier","Marquer comme créé","Valider dans le jeu","Supprimer"};new AlertDialog.Builder(this).setTitle(a.getName()).setItems(actions,(d,w)->{if(w==0)editAsset(a.getId());else if(w==1){database.updateAssetStatus(a.getId(),Asset.STATUS_CREATED);loadAssets();}else if(w==2){database.updateAssetStatus(a.getId(),Asset.STATUS_INTEGRATED);loadAssets();}else confirmDelete(a);}).show();}
    private void confirmDelete(Asset a){new AlertDialog.Builder(this).setTitle("Supprimer l’asset ?").setMessage(a.getName()+" sera supprimé de la liste.").setNegativeButton("Annuler",null).setPositiveButton("Supprimer",(d,w)->{database.deleteAsset(a.getId());loadAssets();}).show();}
}
