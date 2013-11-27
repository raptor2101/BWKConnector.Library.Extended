package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.NotificationService;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.NavigationButtonAdapter;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Task.GamesLoaderTask;

public abstract class AbstractGameListingActivity extends Activity implements GamesLoaderTask.ResultListener {
  private int mLayoutId;
  protected AbstractGameListingActivity(int layoutId){
    mLayoutId = layoutId;
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(mLayoutId);
    
    NavigationButtonAdapter adapter =new NavigationButtonAdapter(this, R.menu.navigation_menu);
    
    ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.action_bar_layout);
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_CUSTOM);
    
    AbsListView listView = (AbsListView) findViewById(R.id.navigation_menu);
    listView.setAdapter(adapter);
    
    listView = (AbsListView) findViewById(R.id.game_listing);
    listView.setAdapter(getGamesAdapter());
  }
  

  @Override
  protected void onResume() {
    super.onResume();
    
    ApplicationSettings settings = new ApplicationSettings(this);
    
    if(settings.getEmail().equals(ApplicationSettings.EmptyResult)){
      startSettingsActivity();
      return;
    }
    
    AbstractConnectorApp app = (AbstractConnectorApp) getApplication();
    long lastLoad = app.getDatabase().getLastPersistTimestamp();
    loadGames(SystemClock.elapsedRealtime()-lastLoad>settings.getRefreshCylce());
  }

  private void loadGames(boolean forceReload) {
    ApplicationSettings settings = new ApplicationSettings(this);
    
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.action_bar_progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    
    GamesLoaderTask task = new GamesLoaderTask(this, settings.getEmail(), settings.getPassword(), this);
    task.execute(new Boolean[]{forceReload});
    
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.action_bar_menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if(item.getItemId() == R.id.action_settings){
      startSettingsActivity();
    } else if (item.getItemId() == R.id.action_refresh){
      loadGames(true);
    }
    return super.onMenuItemSelected(featureId, item);
  }
  
  @Override
  public void handleResult(GamesLoaderTask.Result result) {
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.action_bar_progress_bar);
    progressBar.setVisibility(View.GONE);
    if(result != null){
      setGames(result.getGames());
      NotificationService.reset(this);
    }
  }
  
  protected abstract void startSettingsActivity();
  
  protected abstract ListAdapter getGamesAdapter();
  
  protected abstract void setGames(List<Game> myGames);
}
