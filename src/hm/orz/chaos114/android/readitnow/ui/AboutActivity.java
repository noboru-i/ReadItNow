package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);

		try {
			final WebView appNameView = (WebView) findViewById(R.id.web_about);
			appNameView.loadUrl("file:///android_asset/about.html");

			final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			final String appName = "Read It Now Ver." + packageInfo.versionName;
		} catch (final NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
