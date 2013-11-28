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
import de.raptor2101.BattleWorldsKronos.Connector.Gui.NavigationButtonAdapter;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameInfo;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameListing;
import de.raptor2101.BattleWorldsKronos.Connector.Task.GameListingLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Task.GameListingLoaderTask.ResultListener;

public abstract class AbstractGameListingActivity extends Activity implements ResultListener {
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
    listView.setAdapter(getGameInfoAdapter());
  }
  

  @Override
  protected void onResume() {
    super.onResume();
    
    ApplicationSettings settings = new ApplicationSettings(this);
    AbstractConnectorApp app = (AbstractConnectorApp) getApplication();
    
    if(settings.getEmail().equals(ApplicationSettings.EmptyResult)){
      startSettingsActivity();
      return;
    }
    
    if(app.getTimestampResultStored() == 0 || SystemClock.elapsedRealtime()-app.getTimestampResultStored()>settings.getRefreshCylce()){
      loadGameInfos();
    } else {
      handleResult(app.getResult());
    }
  }

  private void loadGameInfos() {
    ApplicationSettings settings = new ApplicationSettings(this);
    
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.action_bar_progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    
    GameListingLoaderTask task = new GameListingLoaderTask(AndroidHttpClient.newInstance((String) getText(R.string.app_name)), settings.getEmail(), settings.getPassword(), this);
    task.execute(new Void[0]);
    
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
      loadGameInfos();
    }
    return super.onMenuItemSelected(featureId, item);
  }
  
  @Override
  public void handleResult(GameListing result) {
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.action_bar_progress_bar);
    progressBar.setVisibility(View.GONE);
    if(result != null){
      setGameInfos(result.getMyGames());
      AbstractConnectorApp app = (AbstractConnectorApp) getApplication();
      app.storeResult(result);
      NotificationService.reset(this);
    }
  }
  
  protected abstract void startSettingsActivity();
  
  protected abstract ListAdapter getGameInfoAdapter();
  
  protected abstract void setGameInfos(List<GameInfo> myGames);
}
