package hm.orz.chaos114.android.readitnow.appwidget;

import hm.orz.chaos114.android.readitnow.util.ArticleListFileUtil;
import hm.orz.chaos114.android.readitnow.util.WidgetUtil;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class CountWidget extends AppWidgetProvider {
	private static final String TAG = CountWidget.class.getSimpleName();

	public static final String EXTRA_APP_WIDGET_ID = "appWidgetId";

	@Override
	public void onEnabled(final Context context) {
		Log.d(TAG, "#onEnabled");
	}

	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		Log.d(TAG, "#onUpdate");
		Log.d(TAG, "appWidgetIds.length = " + appWidgetIds.length);
		Log.d(TAG, "appWidgetIds[0] = " + appWidgetIds[0]);

		for (final int appWidgetId : appWidgetIds) {
			WidgetUtil.update(context, appWidgetId);
		}
	}

	@Override
	public void onDeleted(final Context context, final int[] appWidgetIds) {
		Log.d(TAG, "#onDeleted");

		// 記事リストを削除する
		for (final int appWidgetId : appWidgetIds) {
			final ArticleListFileUtil util = new ArticleListFileUtil(context,
					appWidgetId);
			util.deleteList();
		}
	}

	@Override
	public void onDisabled(final Context context) {
		Log.d(TAG, "#onDisabled");

		// 記事リストを全て削除する
		ArticleListFileUtil.deleteListAll(context);
	}
}
