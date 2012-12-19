package hm.orz.chaos114.android.readitnow.util;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.ui.ArticleListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetUtil {

	public static void update(final Context context, final int appWidgetId) {

		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.widget_layout);
		final Intent intent = new Intent(context.getApplicationContext(),
				ArticleListActivity.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				context.getApplicationContext(),
				appWidgetId, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.text_view, pendingIntent);

		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
}
