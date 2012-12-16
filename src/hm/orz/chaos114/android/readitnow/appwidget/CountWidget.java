package hm.orz.chaos114.android.readitnow.appwidget;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.ui.ArticleListActivity;
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
			Intent intent = new Intent(context, ArticleListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.text_view, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
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
}
