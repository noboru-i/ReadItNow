package hm.orz.chaos114.android.readitnow.util;

import hm.orz.chaos114.android.readitnow.appwidget.CountWidget;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import pocket4j.Item;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

public class ArticleListFileUtil {
	private final Context mContext;
	private final int mAppWidgetId;

	public ArticleListFileUtil(final Context context, final int appWidgetId) {
		mContext = context;
		mAppWidgetId = appWidgetId;
	}

	public void deleteList() {
		mContext.deleteFile(getFileName());
	}

	public void saveList(final List<Item> items) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(mContext.openFileOutput(getFileName(),
					Context.MODE_PRIVATE));
			oos.writeObject(items);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (final IOException e) {
				// no-op
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Item> loadList() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(mContext.openFileInput(getFileName()));
			final Object object = ois.readObject();
			return (List<Item>) object;
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (final StreamCorruptedException e) {
			throw new RuntimeException(e);
		} catch (final EOFException e) {
			return null;
		} catch (final FileNotFoundException e) {
			// 初期状態ではファイルは存在しない
			return null;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (final IOException e) {
				// no-op
			}
		}
	}

	private String getFileName() {
		return "ArticleList" + mAppWidgetId + ".dat";
	}

	public static void deleteListAll(final Context context) {
		final ComponentName thisWidget = new ComponentName(context,
				CountWidget.class);
		final int[] appWidgetIds = AppWidgetManager.getInstance(context)
				.getAppWidgetIds(thisWidget);
		// 追加済みの全てのwidgetに対してループ
		for (final int appWidgetId : appWidgetIds) {

			// 保存している記事リストを削除
			final ArticleListFileUtil util = new ArticleListFileUtil(context,
					appWidgetId);
			util.deleteList();

			// widgetの更新
			WidgetUtil.update(context, appWidgetId);
		}
	}
}
