package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.util.WidgetUtil;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;

public class SettingActivity extends PreferenceActivity {
	private static final String TAG = SettingActivity.class.getSimpleName();

	private int mAppWidgetId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "#onCreate");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.query_preference);

		mAppWidgetId = getIntent().getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// デフォルト値が返却された場合、終了
			finish();
			return;
		}

		final ButtonPreference preference = (ButtonPreference) findPreference("complete_button");
		preference.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finishConfigure();
			}
		});
	}

	private void finishConfigure() {
		Log.d(TAG, "#finishConfigure");

		WidgetUtil.update(this, mAppWidgetId);

		// Make sure we pass back the original appWidgetId
		final Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
}
