package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.util.ArticleListFileUtil;
import hm.orz.chaos114.android.readitnow.util.SettingPreferenceUtil;
import hm.orz.chaos114.android.readitnow.util.WidgetUtil;
import hm.orz.chaos114.android.util.PreferenceUtil;

import java.util.List;

import pocket4j.Item;
import pocket4j.Pocket;
import pocket4j.RetrieveOptions;
import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;

public class ArticleListActivity extends SherlockFragmentActivity {
	private static final String TAG = ArticleListActivity.class.getSimpleName();

	private int mAppWidgetId;

	private Pocket mPocket;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// デフォルト値が返却された場合、終了
			finish();
			return;
		}

		// Pocketインスタンスを生成
		final String accessToken = new PreferenceUtil(this)
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		final Configuration configuration = new Configuration(this);
		final Authorization authorization = new Authorization(accessToken);
		mPocket = new Pocket(authorization, configuration);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!hasAuthorization()) {
			// 未認証画面を表示
			setContentView(R.layout.activity_article_list_unauthorize);
			final TextView textView = (TextView) findViewById(R.id.main_test);
			textView.setText(R.string.dialog_list_without_auth);

			// 未認証時の処理
			new AlertDialog.Builder(this)
					.setMessage(R.string.dialog_list_without_auth)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									startAuthActivity();
								}
							}).show();
			return;
		}

		// 認証されている場合は、リストを表示
		final String accessToken = new PreferenceUtil(this)
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		final Authorization authorization = new Authorization(accessToken);
		mPocket.setAuthorization(authorization);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(
			final com.actionbarsherlock.view.Menu menu) {
		final MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			final com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			final ArticleListFileUtil fileUtil = new ArticleListFileUtil(this,
					mAppWidgetId);
			fileUtil.deleteList();
			init();
			break;
		case R.id.menu_account:
			startAuthActivity();
			break;
		case R.id.menu_setting:
			startSettingActivity();
			break;
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("mAppWidgetId", mAppWidgetId);
		outState.putSerializable("mPocket", mPocket);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mAppWidgetId = savedInstanceState.getInt("mAppWidgetId");
		mPocket = (Pocket) savedInstanceState.getSerializable("mPocket");
	}

	private void init() {
		if (!hasAuthorization()) {
			return;
		}
		ListView list = (ListView) findViewById(R.id.article_list);
		if (list == null) {
			setContentView(R.layout.activity_article_list);
			list = (ListView) findViewById(R.id.article_list);
		}

		final AsyncTask<Void, Void, List<Item>> task = new AsyncTask<Void, Void, List<Item>>() {

			ProgressDialog dialog;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(ArticleListActivity.this);
				dialog.setMessage(getString(R.string.dialog_loading_message));
				dialog.show();
			}

			@Override
			protected List<Item> doInBackground(final Void... params) {
				final ArticleListFileUtil fileUtil = new ArticleListFileUtil(
						ArticleListActivity.this, mAppWidgetId);
				final List<Item> loadItems = fileUtil.loadList();
				if (loadItems != null) {
					// 保存してある情報があれば、それを表示する
					return loadItems;
				}

				final SettingPreferenceUtil preferenceUtil = new SettingPreferenceUtil(
						ArticleListActivity.this, mAppWidgetId);
				final RetrieveOptions options = RetrieveOptions
						.createInstance(preferenceUtil
								.getAll());
				final List<Item> items = mPocket.get(options);
				fileUtil.saveList(items);
				return items;
			}

			@Override
			protected void onPostExecute(final List<Item> result) {
				final ArticleAdapter adapter = new ArticleAdapter(
						ArticleListActivity.this, 0, result);
				final ListView list = (ListView) findViewById(R.id.article_list);
				list.setAdapter(adapter);

				WidgetUtil.updateWidget(ArticleListActivity.this, mAppWidgetId,
						Integer.toString(result.size()));

				dialog.dismiss();
			}
		};
		task.execute((Void) null);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final Item item = (Item) parent.getItemAtPosition(position);

				// ブラウザを起動
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(item.getResolvedUrl()));
				startActivity(intent);
			}
		});
	}

	private void startAuthActivity() {
		startActivity(new Intent(this, AuthActivity.class));
	}

	private void startSettingActivity() {
		final Intent intent = new Intent(this, SettingActivity.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		startActivity(intent);
	}

	private boolean hasAuthorization() {
		final String accessToken = new PreferenceUtil(this)
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		if (accessToken == null) {
			return false;
		}
		return true;
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
