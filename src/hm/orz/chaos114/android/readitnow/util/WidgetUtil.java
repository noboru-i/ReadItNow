package hm.orz.chaos114.android.readitnow.util;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.ui.ArticleListActivity;
import hm.orz.chaos114.android.readitnow.ui.AuthActivity;
import hm.orz.chaos114.android.util.PreferenceUtil;

import java.util.Map;

import pocket4j.Pocket;
import pocket4j.RetrieveOptions;
import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class WidgetUtil {

	public static void update(final Context context, final int appWidgetId) {
		// 一旦、"-"で更新（データ取得中もclick時の挙動を可能にするため）
		updateWidget(context, appWidgetId, "-");

		final PreferenceUtil preferenceUtil = new PreferenceUtil(context);
		final String accessToken = preferenceUtil
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		if (accessToken == null) {
			// 認証が済んでない場合終了
			return;
		}

		final Configuration configuration = new Configuration(context);
		final Authorization authorization = new Authorization();
		authorization.setAccessToken(accessToken);
		final Pocket pocket = new Pocket(authorization, configuration);
		final RetrieveOptions options = RetrieveOptions
				.createInstance((Map<String, String>) preferenceUtil.getAll());

		new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(final Void... params) {
				return pocket.count(options);
			}

			@Override
			protected void onPostExecute(final Integer result) {
				updateWidget(context, appWidgetId, Integer.toString(result));
			}
		}.execute((Void) null);
	}

	public static void updateWidget(final Context context,
			final int appWidgetId, final String count) {
		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.widget_layout);

		// onClick時の挙動を設定
		remoteViews.setOnClickPendingIntent(R.id.text_view,
				createOnClickPendingIntent(context, appWidgetId));
		// カウントを設定
		remoteViews.setTextViewText(R.id.text_view, count);

		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	private static PendingIntent createOnClickPendingIntent(
			final Context context,
			final int appWidgetId) {
		final Intent intent = new Intent(context.getApplicationContext(),
				ArticleListActivity.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				context.getApplicationContext(), appWidgetId, intent, 0);

		return pendingIntent;
	}
}
