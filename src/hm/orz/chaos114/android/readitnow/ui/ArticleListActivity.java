package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.SubMenu;

public class ArticleListActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);
	}

	@Override
	public boolean onCreateOptionsMenu(
			final com.actionbarsherlock.view.Menu menu) {
		final SubMenu sub = menu.addSubMenu("Setting").setIcon(
				android.R.drawable.ic_menu_preferences);
		sub.add(Menu.NONE, 1, Menu.NONE, "Account");
		sub.add(Menu.NONE, 2, Menu.NONE, "Setting");
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
			final com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			startActivity(new Intent(this, AuthActivity.class));
			break;
		case 2:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		}
		return true;
	}
}
