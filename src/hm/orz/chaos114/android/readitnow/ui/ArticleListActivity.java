package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.util.PreferenceUtil;
import hm.orz.chaos114.android.util.ServerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pocket4j.Item;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.SubMenu;

public class ArticleListActivity extends SherlockFragmentActivity {
	private static final String TAG = ArticleListActivity.class.getSimpleName();

	private int mAppWidgetId;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);

		final Intent intent = getIntent();
		mAppWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// デフォルト値が返却された場合、終了
			finish();
			return;
		}

		init();
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
			final Intent intent = new Intent(this, SettingActivity.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			startActivity(intent);
			break;
		}
		return true;
	}

	private void init() {
		final AsyncTask<Void, Void, List<Item>> task = new AsyncTask<Void, Void, List<Item>>() {

			ProgressDialog dialog = new ProgressDialog(ArticleListActivity.this);

			@Override
			protected void onPreExecute() {
				dialog.setMessage(getString(R.string.dialog_loading_message));
				dialog.show();
			}

			@Override
			protected List<Item> doInBackground(final Void... params) {
				final List<Item> items = fetchArticle();
				return items;
			}

			@Override
			protected void onPostExecute(final List<Item> result) {
				final ArticleAdapter adapter = new ArticleAdapter(
						ArticleListActivity.this, 0, result);
				final ListView list = (ListView) findViewById(R.id.article_list);
				list.setAdapter(adapter);

				dialog.dismiss();
			}
		};
		task.execute((Void) null);

		final ListView list = (ListView) findViewById(R.id.article_list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final Item item = (Item) parent.getItemAtPosition(position);
				Log.d(TAG, "item = " + item);

				// ブラウザを起動
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(item.getResolvedUrl()));
				startActivity(intent);
			}
		});
	}

	private List<Item> fetchArticle() {
		final PreferenceUtil preferenceUtil = new PreferenceUtil(this);
		final String accessToken = preferenceUtil
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		final String url = getString(R.string.url_v3_get);
		final String consumerKey = getString(R.string.pocket_api_key);

		final JSONObject params = new JSONObject();
		try {
			params.put("consumer_key", consumerKey);
			params.put("access_token", accessToken);
			params.put("detailType", "complete");
			params.put("sort", "newest");
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}

		final List<Item> items = new ArrayList<Item>();
		try {
			final String response = ServerUtil.postJson(url, params);
			final JSONObject object = new JSONObject(response);
			Log.d(TAG, object.toString(2));
			final JSONObject list = object.getJSONObject("list");
			final Iterator<?> ite = list.keys();
			while (ite.hasNext()) {
				final Item item = new Item(list.getJSONObject((String) ite
						.next()));
				Log.d(TAG, "item = " + item);
				items.add(item);
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}

		return items;
	}

	class ArticleAdapter extends ArrayAdapter<Item> {

		Context mContext;
		List<Item> mItems;

		public ArticleAdapter(final Context context,
				final int textViewResourceId, final List<Item> objects) {
			super(context, textViewResourceId, objects);
			mContext = context;
			mItems = objects;
		}

		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				final LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.row_data, null);
			}

			final Item item = mItems.get(position);
			final TextView titleTextView = (TextView) view
					.findViewById(R.id.row_title);
			titleTextView.setText(item.getResolvedTitle());

			final TextView excerptTextView = (TextView) view
					.findViewById(R.id.row_excerpt);
			excerptTextView.setText(item.getExcerpt());
			return view;
		}

	}
}
