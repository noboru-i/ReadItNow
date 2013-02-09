package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockFragmentActivity {
	private static final String TAG = AboutActivity.class.getSimpleName();

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);

		// アクションバーに戻るを設定
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final WebView appNameView = (WebView) findViewById(R.id.web_about);
		appNameView.getSettings().setJavaScriptEnabled(true);
		appNameView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				// fileスキーマは読み込む
				if (url.startsWith("file://")) {
					return false;
				}

				// 指定以外のURLは外部ブラウザを起動
				final Uri uri = Uri.parse(url);
				final Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				return true;
			}

			@Override
			public void onPageFinished(final WebView view, final String url) {
				Log.d(TAG, "#onPageFinished");

				final PackageInfo packageInfo;
				try {
					// versionNameを設定する
					packageInfo = getPackageManager().getPackageInfo(
							getPackageName(), PackageManager.GET_META_DATA);
				} catch (final NameNotFoundException e) {
					throw new RuntimeException(e);
				}
				view.loadUrl("javascript:setVersion('"
						+ packageInfo.versionName + "')");
			}
		});

		// assetのHTMLを読み込み
		appNameView.loadUrl("file:///android_asset/about.html");
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// 戻る
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
