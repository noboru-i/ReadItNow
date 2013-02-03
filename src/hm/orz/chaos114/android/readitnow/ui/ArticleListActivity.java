package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.util.ArticleListFileUtil;
import hm.orz.chaos114.android.readitnow.util.SettingPreferenceUtil;
import hm.orz.chaos114.android.readitnow.util.WidgetUtil;
import hm.orz.chaos114.android.util.PreferenceUtil;

import java.io.IOException;
import java.util.List;

import pocket4j.Item;
import pocket4j.Pocket;
import pocket4j.action.Action;
import pocket4j.action.modify.BaseModifyAction;
import pocket4j.action.retrieve.RetrieveAction;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;

public class ArticleListActivity extends SherlockFragmentActivity {
	private static final String TAG = ArticleListActivity.class.getSimpleName();

	private int mAppWidgetId;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "#onCreate");
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// デフォルト値が返却された場合、終了
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!hasAuthorization()) {
			// 未認証画面を表示
			setContentView(R.layout.activity_article_list_unauthorize);
			final TextView textView = (TextView) findViewById(R.id.text_not_authorize);
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
		init();
	}

	private Pocket createPocketInstance() {
		final String accessToken = new PreferenceUtil(this)
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		final Configuration configuration = new Configuration(this);
		final Authorization authorization = new Authorization(accessToken);
		return new Pocket(authorization, configuration);
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
		case R.id.menu_about:
			startAboutActivity();
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("mAppWidgetId", mAppWidgetId);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mAppWidgetId = savedInstanceState.getInt("mAppWidgetId");
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

		new AsyncTask<Void, Void, List<Item>>() {
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
				if (loadItems != null && loadItems.size() != 0) {
					// 保存してある情報があれば、それを表示する
					return loadItems;
				}

				final SettingPreferenceUtil preferenceUtil = new SettingPreferenceUtil(
						ArticleListActivity.this, mAppWidgetId);
				final RetrieveAction options = RetrieveAction
						.createInstance(preferenceUtil.getAll());
				List<Item> items;
				try {
					final Pocket pocket = createPocketInstance();
					items = pocket.retrieve(options);
				} catch (final IOException e) {
					// 通信異常時はnullを返す
					return null;
				}
				fileUtil.saveList(items);
				return items;
			}

			@Override
			protected void onPostExecute(final List<Item> result) {
				if (result == null) {
					// 通信異常時はエラーを表示する
					Toast.makeText(ArticleListActivity.this,
							R.string.toast_network_error,
							Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
				final ArticleAdapter adapter = new ArticleAdapter(
						ArticleListActivity.this, 0, result);
				final ListView list = (ListView) findViewById(R.id.article_list);
				list.setAdapter(adapter);

				WidgetUtil.updateWidget(ArticleListActivity.this, mAppWidgetId,
						Integer.toString(result.size()));

				dialog.dismiss();

				if (result.size() == 0) {
					// 表示件数が0の場合はtoastを表示
					Toast.makeText(ArticleListActivity.this,
							R.string.toast_data_not_found,
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute((Void) null);

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
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				// 編集ダイアログを表示
				final Item item = (Item) parent.getItemAtPosition(position);
				showEditDialog(item);
				return true;
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

	private void startAboutActivity() {
		final Intent intent = new Intent(this, AboutActivity.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		startActivity(intent);
	}

	private void showEditDialog(final Item item) {
		final List<BaseModifyAction> enableAction = item
				.getEnableModifyAction();
		final String[] actionNames = new String[enableAction.size()];
		for (int i = 0; i < enableAction.size(); i++) {
			actionNames[i] = enableAction.get(i).getActionName();
		}
		new AlertDialog.Builder(ArticleListActivity.this).setTitle("Action")
				.setItems(actionNames, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						final Action selectedAction = item
								.getEnableModifyAction().get(which);

						// modifyリクエストをbackgroundで実行するためのタスク
						new AsyncTask<Void, Void, Boolean>() {
							ProgressDialog dialog;

							@Override
							protected void onPreExecute() {
								dialog = new ProgressDialog(
										ArticleListActivity.this);
								dialog.setMessage(getString(R.string.dialog_loading_message));
								dialog.show();
							}

							@Override
							protected Boolean doInBackground(
									final Void... params) {
								try {
									// 更新処理を実行
									final Pocket pocket = createPocketInstance();
									pocket.modify(selectedAction);
								} catch (final IOException e) {
									return false;
								}
								return true;
							}

							@Override
							protected void onPostExecute(final Boolean result) {
								if (!result) {
									// IO例外時はエラーメッセージを表示して終了
									Toast.makeText(ArticleListActivity.this,
											R.string.toast_network_error,
											Toast.LENGTH_LONG)
											.show();
									dialog.dismiss();
									return;
								}

								// 成功した場合は画面データの再取得・再描画
								dialog.dismiss();
								final ArticleListFileUtil fileUtil = new ArticleListFileUtil(
										ArticleListActivity.this, mAppWidgetId);
								fileUtil.deleteList();
								init();
							}
						}.execute((Void) null);
					}
				}).show();
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
