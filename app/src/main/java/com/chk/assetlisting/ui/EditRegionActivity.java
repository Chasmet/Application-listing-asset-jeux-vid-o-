package com.chk.assetlisting.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.chk.assetlisting.R;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Region;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditRegionActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID="game_id",EXTRA_REGION_ID="region_id";
    private static final String[] TYPES={"Région","Île","Village","Ville","Donjon","Niveau","Zone maritime","Bâtiment","Autre"};
    private DatabaseHelper database;private long gameId,regionId;private String imageUri="";private ImageView imageRegion;private TextInputEditText editName,editNotes;private Spinner spinnerType;
    private final ActivityResultLauncher<String[]> imagePicker=registerForActivityResult(new ActivityResultContracts.OpenDocument(),this::setImage);
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_edit_region);database=new DatabaseHelper(this);gameId=getIntent().getLongExtra(EXTRA_GAME_ID,0);regionId=getIntent().getLongExtra(EXTRA_REGION_ID,0);if(gameId==0){finish();return;}MaterialToolbar toolbar=findViewById(R.id.toolbar);imageRegion=findViewById(R.id.imageRegion);editName=findViewById(R.id.editName);editNotes=findViewById(R.id.editNotes);spinnerType=findViewById(R.id.spinnerType);MaterialButton choose=findViewById(R.id.buttonChooseImage);MaterialButton save=findViewById(R.id.buttonSave);ArrayAdapter<String>a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,TYPES);a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinnerType.setAdapter(a);toolbar.setNavigationOnClickListener(v->finish());toolbar.setTitle(regionId==0?"Nouvelle région":"Modifier la région");choose.setOnClickListener(v->imagePicker.launch(new String[]{"image/*"}));imageRegion.setOnClickListener(v->imagePicker.launch(new String[]{"image/*"}));save.setOnClickListener(v->saveRegion());if(regionId!=0)loadRegion();}
    private void loadRegion(){Region r=database.getRegion(regionId);if(r==null){finish();return;}editName.setText(r.getName());editNotes.setText(r.getNotes());imageUri=r.getImageUri();for(int i=0;i<TYPES.length;i++)if(TYPES[i].equalsIgnoreCase(r.getType()))spinnerType.setSelection(i);showImage();}
    private void setImage(Uri uri){if(uri==null)return;try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(SecurityException ignored){}imageUri=uri.toString();showImage();}
    private void showImage(){imageRegion.setImageResource(R.drawable.ic_image);if(TextUtils.isEmpty(imageUri))return;try{imageRegion.setImageURI(Uri.parse(imageUri));}catch(Exception ignored){imageRegion.setImageResource(R.drawable.ic_image);}}
    private void saveRegion(){String name=value(editName);if(TextUtils.isEmpty(name)){editName.setError("Le nom est obligatoire");editName.requestFocus();return;}Region r=new Region();r.setId(regionId);r.setGameId(gameId);r.setName(name);r.setType(String.valueOf(spinnerType.getSelectedItem()));r.setImageUri(imageUri);r.setNotes(value(editNotes));database.saveRegion(r);Toast.makeText(this,"Région enregistrée",Toast.LENGTH_SHORT).show();setResult(RESULT_OK);finish();}
    private String value(TextInputEditText e){return e.getText()==null?"":e.getText().toString().trim();}
}
