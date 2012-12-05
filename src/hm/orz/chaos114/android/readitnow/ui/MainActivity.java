package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "#onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri data = intent.getData();
			Log.d(TAG, "data = " + data);
			if (data != null
					&& "authorizationFinished".equals(data
							.getEncodedAuthority())) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				final String requestToken = preferences.getString(
						"REQUEST_TOKEN", null);
				AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							getAccessToken(requestToken);
						} catch (Exception e) {
							return false;
						}
						return true;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						if (!result) {
							// 認証失敗
							Toast.makeText(MainActivity.this, "認証に失敗しました",
									Toast.LENGTH_LONG).show();
							return;
						}
						SharedPreferences preferences = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						String username = preferences.getString("USERNAME",
								null);
						TextView view = (TextView) MainActivity.this
								.findViewById(R.id.main_test);
						view.setText("Hello " + username);
					}
				};
				task.execute((Void) null);
				return;
			}
		}

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// request tokenを取得する
				String requestToken = getRequestToken();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor editor = preferences.edit();
				editor.putString("REQUEST_TOKEN", requestToken);
				editor.commit();

				// 認証画面を表示する
				startOauthActivity(requestToken);
				return null;
			}
		};
		task.execute((Void) null);
	}

	private String getRequestToken() {
		String url = getString(R.string.url_v3_request);
		JSONObject param = new JSONObject();
		try {
			param.put("consumer_key", getString(R.string.pocket_api_key));
			param.put("redirect_uri", "readitnow://authorizationFinished");
			String response = postJson(url, param);
			JSONObject respJson = new JSONObject(response);
			String requestToken = respJson.getString("code");
			return requestToken;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void startOauthActivity(String requestToken) {
		Uri.Builder builder = new Uri.Builder();
		builder.appendQueryParameter("request_token", requestToken);
		builder.appendQueryParameter("redirect_uri",
				"readitnow://authorizationFinished");
		String queryString = builder.build().toString();
		String url = getString(R.string.url_authrize);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url + queryString));
		startActivity(intent);
	}

	private void getAccessToken(String requestToken) {
		String url = getString(R.string.url_v3_authrize);
		JSONObject param = new JSONObject();
		try {
			param.put("consumer_key", getString(R.string.pocket_api_key));
			param.put("code", requestToken);
			String response = postJson(url, param);
			JSONObject respJson = new JSONObject(response);
			String accessToken = respJson.getString("access_token");
			String username = respJson.getString("username");

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = preferences.edit();
			editor.putString("USERNAME", username);
			editor.putString("ACCESS_TOKEN", accessToken);
			editor.commit();
			Log.d(TAG, "accessToken = " + accessToken);
			Log.d(TAG, "username = " + username);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * APサーバにPOSTリクエストを発行する。
	 * 
	 * @param endpoint リクエストURL
	 * @param params リクエストパラメータ
	 * @return レスポンス文字列
	 * @throws IOException 通信例外
	 */
	private static String postJson(final String endpoint,
			final JSONObject params) throws IOException {
		Log.i(TAG, "endpoint = " + endpoint);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(endpoint);

		try {
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPost.setHeader("X-Accept", "application/json");
			StringEntity se = new StringEntity(params.toString());
			httpPost.setEntity(se);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		HttpResponse response = httpClient.execute(httpPost);
		String responseString = EntityUtils.toString(response.getEntity());
		Log.i(TAG, "response = " + responseString);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new IOException("Post failed. statusCode=" + statusCode);
		}

		return responseString;
	}
}
