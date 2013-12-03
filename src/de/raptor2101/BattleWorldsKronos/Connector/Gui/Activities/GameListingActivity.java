package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class GameListingActivity extends AbstractGameListingActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.action_bar_layout);
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_CUSTOM);
    
    TextView textView = (TextView) findViewById(R.id.action_bar_title);
    textView.setText(R.string.title_activity_games);
  }
  
  @Override
  protected ProgressBar GetProgressBar() {
    return (ProgressBar) findViewById(R.id.action_bar_progress_bar);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.action_bar_menu, menu);
    return true;
  }
}
