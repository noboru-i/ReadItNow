package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.util.ArticleListFileUtil;
import hm.orz.chaos114.android.util.PreferenceUtil;
import hm.orz.chaos114.android.util.ServerUtil;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AuthActivity extends SherlockFragmentActivity {
	private static final String TAG = AuthActivity.class.getSimpleName();

	private static final String SCHEME = "readitnow";
	private static final String AUTHORIZATION_FINISHED = "authorizationFinished";

	public static final String PREFERENCE_REQUEST_TOKEN = "request_token";
	public static final String PREFERENCE_ACCESS_TOKEN = "access_token";
	public static final String PREFERENCE_USER_NAME = "user_name";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		final PreferenceUtil preferenceUtil = new PreferenceUtil(this);
		final String userName = preferenceUtil.getString(PREFERENCE_USER_NAME);

		if (userName != null) {
			// 認証済み
			showAuthorizedView();
		} else {
			// 未認証
			showUnauthorizedView();
		}
	}

	/**
	 * 認証済み画面を表示する。
	 */
	private void showAuthorizedView() {
		final PreferenceUtil preferenceUtil = new PreferenceUtil(
				AuthActivity.this);

		// auth_button の設定
		final Button authButton = (Button) findViewById(R.id.auth_button);
		authButton.setText(R.string.button_logout);
		authButton.setEnabled(true);
		authButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				new AlertDialog.Builder(AuthActivity.this)
						.setMessage(R.string.dialog_confirm_logout)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int which) {
										// ログアウト処理
										logout();
									}
								})
						.setNegativeButton(android.R.string.cancel, null)
						.show();
			}
		});

		// main_text の設定
		final String userName = preferenceUtil.getString(PREFERENCE_USER_NAME);
		final TextView view = (TextView) findViewById(R.id.text_auth_message);
		view.setText("USERNAME : " + userName);
	}

	/**
	 * 未認証画面を表示する。
	 */
	private void showUnauthorizedView() {
		final PreferenceUtil preferenceUtil = new PreferenceUtil(
				AuthActivity.this);

		// auth_button のイベントを設定
		final Button authButton = (Button) findViewById(R.id.auth_button);
		authButton.setText(R.string.button_authorize);
		authButton.setEnabled(true);
		authButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
					private final ProgressDialog dialog = new ProgressDialog(
							AuthActivity.this);

					@Override
					protected void onPreExecute() {
						dialog.setMessage(getString(R.string.dialog_loading_message));
						dialog.show();
					}

					@Override
					protected Void doInBackground(final Void... params) {
						// request tokenを取得する
						final String requestToken = fetchRequestToken();
						preferenceUtil.putString(PREFERENCE_REQUEST_TOKEN,
								requestToken);

						// 認証画面を表示する
						startOauthActivity(requestToken);
						return null;
					}

					@Override
					protected void onPostExecute(final Void result) {
						dialog.dismiss();
					}
				};
				task.execute((Void) null);
			}
		});

		final TextView view = (TextView) findViewById(R.id.text_auth_message);
		view.setText(R.string.label_authorize_message);
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.d(TAG, "#onNewIntent action = " + intent.getAction());

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			final Uri data = intent.getData();
			if (data != null
					&& AUTHORIZATION_FINISHED
							.equals(data.getEncodedAuthority())) {
				authorize(intent);
			}
		}
	}

	private void authorize(final Intent intent) {
		final PreferenceUtil preferenceUtil = new PreferenceUtil(this);
		final String requestToken = preferenceUtil
				.getString(PREFERENCE_REQUEST_TOKEN);

		final AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			private final ProgressDialog dialog = new ProgressDialog(
					AuthActivity.this);

			@Override
			protected void onPreExecute() {
				dialog.setMessage(getString(R.string.dialog_loading_message));
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(final Void... params) {
				try {
					fetchAccessToken(requestToken);
				} catch (final Exception e) {
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(final Boolean result) {
				dialog.dismiss();
				if (!result) {
					// 認証失敗
					Toast.makeText(AuthActivity.this, "認証に失敗しました",
							Toast.LENGTH_LONG).show();
					return;
				}

				// widgetの更新
				// TODO

				// viewの更新
				showAuthorizedView();
			}
		};
		task.execute((Void) null);
	}

	/**
	 * リクエストトークンを取得する。
	 *
	 * @return リクエストトークン
	 */
	private String fetchRequestToken() {
		final String url = getString(R.string.url_v3_request);
		final JSONObject param = new JSONObject();
		try {
			param.put("consumer_key", getString(R.string.pocket_api_key));
			param.put("redirect_uri", SCHEME + "://" + AUTHORIZATION_FINISHED);
			final String response = ServerUtil.postJson(url, param);
			final JSONObject respJson = new JSONObject(response);
			final String requestToken = respJson.getString("code");
			return requestToken;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 認証用にブラウザを起動するIntentを発行する。
	 *
	 * @param requestToken リクエストトークン
	 */
	private void startOauthActivity(final String requestToken) {
		final Uri.Builder builder = new Uri.Builder();
		builder.appendQueryParameter("request_token", requestToken);
		builder.appendQueryParameter("redirect_uri", SCHEME + "://"
				+ AUTHORIZATION_FINISHED);
		final String queryString = builder.build().toString();
		final String url = getString(R.string.url_authrize);
		final Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.setData(Uri.parse(url + queryString));
		startActivity(intent);
	}

	/**
	 * アクセストークンを取得する。
	 *
	 * @param requestToken リクエストトークン
	 */
	private void fetchAccessToken(final String requestToken) {
		final String url = getString(R.string.url_v3_authrize);
		final JSONObject param = new JSONObject();
		try {
			param.put("consumer_key", getString(R.string.pocket_api_key));
			param.put("code", requestToken);
			final String response = ServerUtil.postJson(url, param);
			final JSONObject respJson = new JSONObject(response);
			final String accessToken = respJson.getString("access_token");
			final String username = respJson.getString("username");

			final PreferenceUtil preferenceUtil = new PreferenceUtil(this);
			preferenceUtil.putString(PREFERENCE_USER_NAME, username);
			preferenceUtil.putString(PREFERENCE_ACCESS_TOKEN, accessToken);
			Log.d(TAG, "accessToken = " + accessToken);
			Log.d(TAG, "username = " + username);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ログアウト処理を行う。
	 */
	private void logout() {
		// 認証情報を削除
		final PreferenceUtil preferenceUtil = new PreferenceUtil(this);
		preferenceUtil.remove(PREFERENCE_USER_NAME);
		preferenceUtil.remove(PREFERENCE_ACCESS_TOKEN);

		// 記事リストを削除
		ArticleListFileUtil.deleteListAll(this);

		// 画面表示を未ログイン状態にする
		showUnauthorizedView();
	}

	@Override
	public boolean onCreateOptionsMenu(
			final com.actionbarsherlock.view.Menu menu) {
		// TODO
		return true;
	}
}
