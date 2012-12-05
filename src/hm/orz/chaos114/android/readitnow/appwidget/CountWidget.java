package hm.orz.chaos114.android.readitnow.appwidget;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.ui.MainActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class CountWidget extends AppWidgetProvider {
	private static final String TAG = CountWidget.class.getSimpleName();

	public static final String EXTRA_APP_WIDGET_ID = "appWidgetId";

	public static final String ACTION_WIDGET_TOUCH = "hm.orz.chaos114.android.readitnow.ACTION_WIDGET_TOUCH";

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "#onEnabled");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "#onUpdate");
		Log.d(TAG, "appWidgetIds.length = " + appWidgetIds.length);
		Log.d(TAG, "appWidgetIds[0] = " + appWidgetIds[0]);

		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "#onReceive");
		Log.d(TAG, "intent.getAction() = " + intent.getAction());
		super.onReceive(context, intent);
		if (ACTION_WIDGET_TOUCH.equals(intent.getAction())) {
			onTouch(context, intent);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "#onDeleted");
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "#onDisabled");
	}

	public void onTouch(Context context, Intent intent) {
		Intent startActivityIntent = new Intent(context, MainActivity.class);
		startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startActivityIntent);
	}

	public void updateDisplay(Context context, Intent intent) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		int appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, -1);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
}
