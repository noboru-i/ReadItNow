package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SettingActivity extends PreferenceActivity {
	private static final String TAG = SettingActivity.class.getSimpleName();

	private int mAppWidgetId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.query_preference);

		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		ButtonPreference preference = (ButtonPreference) findPreference("complete_button");
		preference.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finishConfigure();
			}
		});
	}

	private void finishConfigure() {
		Log.d(TAG, "#finishConfigure");
		Log.d(TAG, "mAppWidgetId = " + mAppWidgetId);
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.widget_layout);
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, mAppWidgetId, intent, 0);
		views.setOnClickPendingIntent(R.id.text_view, pendingIntent);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(mAppWidgetId, views);

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
}
