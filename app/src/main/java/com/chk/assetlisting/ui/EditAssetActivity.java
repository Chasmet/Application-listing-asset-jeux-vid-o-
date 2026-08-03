package com.chk.assetlisting.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chk.assetlisting.R;
import com.chk.assetlisting.adapter.ImagePreviewAdapter;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Asset;
import com.chk.assetlisting.model.Region;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class EditAssetActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID="game_id",EXTRA_REGION_ID="region_id",EXTRA_ASSET_ID="asset_id";
    private static final String[] CATEGORIES={"Personnage principal","Ennemi","Boss","PNJ","Animal","Bateau","Véhicule","Arme","Bâtiment","Décoration","Végétation","Texture","Animation","Effet visuel","Interface","Son","Musique","Modèle 3D","Autre"};
    private DatabaseHelper database;private long gameId,initialRegionId,assetId;private final List<String> imageUris=new ArrayList<>();private final List<Long> regionIds=new ArrayList<>();private final List<String> regionNames=new ArrayList<>();private ImagePreviewAdapter imageAdapter;private TextInputEditText editName,editDescription,editFileName,editFilePath;private Spinner spinnerRegion,spinnerCategory;private RadioGroup radioStatus;
    private final ActivityResultLauncher<String[]> imagePicker=registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),this::addImages);
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_edit_asset);database=new DatabaseHelper(this);gameId=getIntent().getLongExtra(EXTRA_GAME_ID,0);initialRegionId=getIntent().getLongExtra(EXTRA_REGION_ID,0);assetId=getIntent().getLongExtra(EXTRA_ASSET_ID,0);if(gameId==0){finish();return;}MaterialToolbar toolbar=findViewById(R.id.toolbar);RecyclerView images=findViewById(R.id.recyclerImages);MaterialButton choose=findViewById(R.id.buttonChooseImages);MaterialButton save=findViewById(R.id.buttonSave);editName=findViewById(R.id.editName);editDescription=findViewById(R.id.editDescription);editFileName=findViewById(R.id.editFileName);editFilePath=findViewById(R.id.editFilePath);spinnerRegion=findViewById(R.id.spinnerRegion);spinnerCategory=findViewById(R.id.spinnerCategory);radioStatus=findViewById(R.id.radioStatus);toolbar.setNavigationOnClickListener(v->finish());toolbar.setTitle(assetId==0?"Nouvel asset":"Modifier l’asset");imageAdapter=new ImagePreviewAdapter(imageUris);images.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));images.setAdapter(imageAdapter);ArrayAdapter<String> ca=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,CATEGORIES);ca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinnerCategory.setAdapter(ca);loadRegions();choose.setOnClickListener(v->imagePicker.launch(new String[]{"image/*"}));save.setOnClickListener(v->saveAsset(false));if(assetId!=0)loadAsset();else selectRegion(initialRegionId);}
    private void loadRegions(){regionIds.clear();regionNames.clear();regionIds.add(0L);regionNames.add("Sans région");for(Region r:database.getRegions(gameId)){regionIds.add(r.getId());regionNames.add(r.getName());}ArrayAdapter<String>a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,regionNames);a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinnerRegion.setAdapter(a);}
    private void loadAsset(){Asset a=database.getAsset(assetId);if(a==null){finish();return;}editName.setText(a.getName());editDescription.setText(a.getDescription());editFileName.setText(a.getFileName());editFilePath.setText(a.getFilePath());selectRegion(a.getRegionId());selectCategory(a.getCategory());if(a.getStatus()==Asset.STATUS_INTEGRATED)((RadioButton)findViewById(R.id.radioIntegrated)).setChecked(true);else if(a.getStatus()==Asset.STATUS_CREATED)((RadioButton)findViewById(R.id.radioCreated)).setChecked(true);else ((RadioButton)findViewById(R.id.radioTodo)).setChecked(true);imageUris.clear();imageUris.addAll(a.getImageUris());imageAdapter.notifyDataSetChanged();}
    private void addImages(List<Uri> uris){if(uris==null)return;for(Uri uri:uris){try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(SecurityException ignored){}String v=uri.toString();if(!imageUris.contains(v))imageUris.add(v);}imageAdapter.notifyDataSetChanged();}
    private void saveAsset(boolean force){String name=value(editName);if(TextUtils.isEmpty(name)){editName.setError("Le nom de l’asset est obligatoire");editName.requestFocus();return;}if(!force){String duplicate=database.findSimilarAssetName(gameId,assetId,name);if(duplicate!=null){new AlertDialog.Builder(this).setTitle("Asset déjà présent").setMessage("« "+duplicate+" » existe déjà dans ce jeu. Veux-tu quand même enregistrer ce doublon ?").setNegativeButton("Annuler",null).setPositiveButton("Enregistrer quand même",(d,w)->saveAsset(true)).show();return;}}Asset a=new Asset();a.setId(assetId);a.setGameId(gameId);int p=spinnerRegion.getSelectedItemPosition();a.setRegionId(p>=0&&p<regionIds.size()?regionIds.get(p):0);a.setName(name);a.setCategory(String.valueOf(spinnerCategory.getSelectedItem()));a.setDescription(value(editDescription));a.setFileName(value(editFileName));a.setFilePath(value(editFilePath));a.setStatus(selectedStatus());a.setImageUris(new ArrayList<>(imageUris));database.saveAsset(a);Toast.makeText(this,"Asset enregistré",Toast.LENGTH_SHORT).show();setResult(RESULT_OK);finish();}
    private int selectedStatus(){int id=radioStatus.getCheckedRadioButtonId();if(id==R.id.radioIntegrated)return Asset.STATUS_INTEGRATED;if(id==R.id.radioCreated)return Asset.STATUS_CREATED;return Asset.STATUS_TODO;}
    private void selectRegion(long id){for(int i=0;i<regionIds.size();i++)if(regionIds.get(i)==id){spinnerRegion.setSelection(i);return;}spinnerRegion.setSelection(0);}
    private void selectCategory(String c){if(c==null)return;for(int i=0;i<CATEGORIES.length;i++)if(CATEGORIES[i].equalsIgnoreCase(c)){spinnerCategory.setSelection(i);return;}spinnerCategory.setSelection(CATEGORIES.length-1);}
    private String value(TextInputEditText e){return e.getText()==null?"":e.getText().toString().trim();}
}
