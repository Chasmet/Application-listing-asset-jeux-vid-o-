package com.chk.assetlisting.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chk.assetlisting.R;
import com.chk.assetlisting.adapter.GameAdapter;
import com.chk.assetlisting.data.DatabaseHelper;
import com.chk.assetlisting.model.Game;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper database; private GameAdapter adapter; private LinearLayout emptyState; private TextView gamesCount,assetsCount,integratedCount;
    private final ActivityResultLauncher<Intent> editGameLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->loadData());
    private final ActivityResultLauncher<String> createBackupLauncher=registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"),this::writeBackup);
    private final ActivityResultLauncher<String[]> openBackupLauncher=registerForActivityResult(new ActivityResultContracts.OpenDocument(),this::readBackup);
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_main);database=new DatabaseHelper(this);MaterialToolbar toolbar=findViewById(R.id.toolbar);RecyclerView recycler=findViewById(R.id.recyclerGames);emptyState=findViewById(R.id.emptyState);gamesCount=findViewById(R.id.textGamesCount);assetsCount=findViewById(R.id.textAssetsCount);integratedCount=findViewById(R.id.textIntegratedCount);ExtendedFloatingActionButton fab=findViewById(R.id.fabAddGame);
        adapter=new GameAdapter(new GameAdapter.Listener(){@Override public void onClick(Game game){Intent i=new Intent(MainActivity.this,GameDetailActivity.class);i.putExtra(GameDetailActivity.EXTRA_GAME_ID,game.getId());startActivity(i);}@Override public void onLongClick(Game game){showGameActions(game);}});recycler.setLayoutManager(new LinearLayoutManager(this));recycler.setAdapter(adapter);fab.setOnClickListener(v->editGameLauncher.launch(new Intent(this,EditGameActivity.class)));
        toolbar.setOnMenuItemClickListener(item->{if(item.getItemId()==R.id.action_export){String date=new SimpleDateFormat("yyyy-MM-dd_HH-mm",Locale.FRANCE).format(new Date());createBackupLauncher.launch("CHK_Asset_Manager_"+date+".json");return true;}if(item.getItemId()==R.id.action_import){new AlertDialog.Builder(this).setTitle("Importer une sauvegarde").setMessage("L’import remplacera les données actuellement présentes dans l’application.").setNegativeButton("Annuler",null).setPositiveButton("Continuer",(d,w)->openBackupLauncher.launch(new String[]{"application/json","text/plain"})).show();return true;}return false;});}
    @Override protected void onResume(){super.onResume();loadData();}
    private void loadData(){if(database==null)return;List<Game> games=database.getGames();adapter.submitList(games);emptyState.setVisibility(games.isEmpty()?View.VISIBLE:View.GONE);int[] s=database.getGlobalStats();gamesCount.setText(String.valueOf(s[0]));assetsCount.setText(String.valueOf(s[1]));integratedCount.setText(String.valueOf(s[2]));}
    private void showGameActions(Game game){String[] actions={"Ouvrir","Modifier","Supprimer"};new AlertDialog.Builder(this).setTitle(game.getName()).setItems(actions,(d,w)->{if(w==0){Intent i=new Intent(this,GameDetailActivity.class);i.putExtra(GameDetailActivity.EXTRA_GAME_ID,game.getId());startActivity(i);}else if(w==1){Intent i=new Intent(this,EditGameActivity.class);i.putExtra(EditGameActivity.EXTRA_GAME_ID,game.getId());editGameLauncher.launch(i);}else confirmDelete(game);}).show();}
    private void confirmDelete(Game game){new AlertDialog.Builder(this).setTitle("Supprimer ce jeu ?").setMessage("Toutes ses régions, ses assets et leurs références seront supprimés.").setNegativeButton("Annuler",null).setPositiveButton("Supprimer",(d,w)->{database.deleteGame(game.getId());loadData();}).show();}
    private void writeBackup(Uri uri){if(uri==null)return;try(OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new IllegalStateException("Impossible d’ouvrir le fichier");out.write(database.exportJson().getBytes(StandardCharsets.UTF_8));Toast.makeText(this,"Sauvegarde exportée",Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"Échec de l’export : "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    private void readBackup(Uri uri){if(uri==null)return;try(InputStream in=getContentResolver().openInputStream(uri);BufferedReader reader=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){StringBuilder json=new StringBuilder();String line;while((line=reader.readLine())!=null)json.append(line).append('\n');database.importJson(json.toString());loadData();Toast.makeText(this,"Sauvegarde restaurée",Toast.LENGTH_LONG).show();}catch(JSONException e){Toast.makeText(this,"Fichier de sauvegarde invalide",Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"Échec de l’import : "+e.getMessage(),Toast.LENGTH_LONG).show();}}
}
