package com.chk.assetlisting.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.chk.assetlisting.R;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Game;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditGameActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_ID="game_id";
    private DatabaseHelper database;private long gameId;private String coverUri="";private ImageView imageCover;private TextInputEditText editName,editDescription;
    private final ActivityResultLauncher<String[]> imagePicker=registerForActivityResult(new ActivityResultContracts.OpenDocument(),this::setCover);
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_edit_game);database=new DatabaseHelper(this);gameId=getIntent().getLongExtra(EXTRA_GAME_ID,0);MaterialToolbar toolbar=findViewById(R.id.toolbar);imageCover=findViewById(R.id.imageCover);editName=findViewById(R.id.editName);editDescription=findViewById(R.id.editDescription);MaterialButton choose=findViewById(R.id.buttonChooseCover);MaterialButton save=findViewById(R.id.buttonSave);toolbar.setNavigationOnClickListener(v->finish());toolbar.setTitle(gameId==0?"Nouveau jeu":"Modifier le jeu");choose.setOnClickListener(v->imagePicker.launch(new String[]{"image/*"}));imageCover.setOnClickListener(v->imagePicker.launch(new String[]{"image/*"}));save.setOnClickListener(v->saveGame());if(gameId!=0)loadGame();}
    private void loadGame(){Game g=database.getGame(gameId);if(g==null){finish();return;}editName.setText(g.getName());editDescription.setText(g.getDescription());coverUri=g.getCoverUri();showCover();}
    private void setCover(Uri uri){if(uri==null)return;try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(SecurityException ignored){}coverUri=uri.toString();showCover();}
    private void showCover(){imageCover.setImageResource(R.drawable.ic_image);if(TextUtils.isEmpty(coverUri))return;try{imageCover.setImageURI(Uri.parse(coverUri));}catch(Exception ignored){imageCover.setImageResource(R.drawable.ic_image);}}
    private void saveGame(){String name=value(editName);if(TextUtils.isEmpty(name)){editName.setError("Le titre du jeu est obligatoire");editName.requestFocus();return;}Game g=new Game();g.setId(gameId);g.setName(name);g.setDescription(value(editDescription));g.setCoverUri(coverUri);database.saveGame(g);Toast.makeText(this,"Jeu enregistré",Toast.LENGTH_SHORT).show();setResult(RESULT_OK);finish();}
    private String value(TextInputEditText e){return e.getText()==null?"":e.getText().toString().trim();}
}
