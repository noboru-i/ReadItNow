package hm.orz.chaos114.android.readitnow.util;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.ui.ArticleListActivity;
import hm.orz.chaos114.android.readitnow.ui.AuthActivity;
import hm.orz.chaos114.android.util.PreferenceUtil;

import java.io.IOException;
import java.util.List;

import pocket4j.Item;
import pocket4j.Pocket;
import pocket4j.action.retrieve.RetrieveAction;
import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class WidgetUtil {

	/**
	 * widgetに表示するカウントを更新する。<br>
	 * カウントはAPIにより最新を取得する。
	 *
	 * @param context コンテキスト
	 * @param appWidgetId ウィジェットのID
	 */
	public static void update(final Context context, final int appWidgetId) {
		// 一旦、"-"で更新（データ取得中もclick時の挙動を可能にするため）
		updateWidget(context, appWidgetId,
				getOldCountFromFile(context, appWidgetId));

		final PreferenceUtil preferenceUtil = new PreferenceUtil(context);
		final String accessToken = preferenceUtil
				.getString(AuthActivity.PREFERENCE_ACCESS_TOKEN);
		if (accessToken == null) {
			// 認証が済んでない場合終了
			return;
		}

		final SettingPreferenceUtil settingPreferenceUtil = new SettingPreferenceUtil(
				context, appWidgetId);

		final Configuration configuration = new Configuration(context);
		final Authorization authorization = new Authorization();
		authorization.setAccessToken(accessToken);
		final Pocket pocket = new Pocket(authorization, configuration);
		final RetrieveAction options = RetrieveAction
				.createInstance(settingPreferenceUtil.getAll());

		new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(final Void... params) {
				List<Item> items;
				try {
					items = pocket.retrieve(options);
				} catch (final IOException e) {
					// 通信異常時はnullを返す
					return null;
				}
				final ArticleListFileUtil fileUtil = new ArticleListFileUtil(
						context, appWidgetId);
				fileUtil.saveList(items);
				return items.size();
			}

			@Override
			protected void onPostExecute(final Integer result) {
				if (result == null) {
					// 通信異常時は処理しない
					return;
				}

				updateWidget(context, appWidgetId, Integer.toString(result));
			}
		}.execute((Void) null);
	}

	private static String getOldCountFromFile(final Context context,
			final int appWidgetId) {
		final ArticleListFileUtil fileUtil = new ArticleListFileUtil(context,
				appWidgetId);
		final List<Item> loadList = fileUtil.loadList();
		String preCount = "-";
		if (loadList != null) {
			preCount = Integer.toString(loadList.size());
		}
		return preCount;
	}

	/**
	 * widgetに表示するカウントを更新する。<br>
	 * カウントは引数より取得する。
	 *
	 * @param context コンテキスト
	 * @param appWidgetId ウィジェットのID
	 * @param count 表示するカウント
	 */
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
			final Context context, final int appWidgetId) {
		final Intent intent = new Intent(context.getApplicationContext(),
				ArticleListActivity.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				context.getApplicationContext(), appWidgetId, intent, 0);

		return pendingIntent;
	}
}
