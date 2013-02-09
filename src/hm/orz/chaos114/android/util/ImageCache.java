package hm.orz.chaos114.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

/**
 * 画像キャッシュを保持するクラス
 *
 * @author noboru
 */
public class ImageCache {
	private final Map<String, Drawable> cache;

	/**
	 * 画像を読み込んだ際に実行されるコールバック
	 *
	 * @author noboru
	 */
	public interface ImageSetListener {
		void setImage(Drawable drawable, String url);
	}

	public ImageCache() {
		cache = new WeakHashMap<String, Drawable>();
	}

	public void attachImage(final String url, final ImageSetListener listener) {
		if (url == null) {
			return;
		}

		if (cache.containsKey(url)) {
			// キャッシュが存在する場合
			listener.setImage(cache.get(url), url);
			return;
		}

		new AsyncTask<String, Void, Drawable>() {
			@Override
			protected Drawable doInBackground(final String... params) {
				final String url = params[0];
				return fetchDrawable(url);
			}

			@Override
			protected void onPostExecute(final Drawable result) {
				if (result == null) {
					return;
				}
				listener.setImage(result, url);
				cache.put(url, result);
			}
		}.execute(url);
	}

	private Drawable fetchDrawable(final String url) {
		InputStream is = null;
		try {
			is = new URL(url).openStream();
			return Drawable.createFromStream(is, "src");
		} catch (final IOException e) {
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException e) {
					// no-op
				}
			}
		}
	}
}
