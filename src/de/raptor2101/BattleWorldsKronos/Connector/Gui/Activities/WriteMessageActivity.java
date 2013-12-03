package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class WriteMessageActivity extends AbstractWriteMessageActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.action_bar_layout);
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_CUSTOM);
    
    TextView textView = (TextView) findViewById(R.id.action_bar_title);
    textView.setText(getTitle());
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.action_bar_menu_write_message, menu);
    return true;
  }
}
