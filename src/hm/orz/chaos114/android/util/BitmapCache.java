package hm.orz.chaos114.android.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * 画像キャッシュを保持するクラス
 *
 * @author noboru
 */
public class BitmapCache implements ImageCache {
	private LruCache<String, Bitmap> mCache;

	public BitmapCache() {
		final int maxSize = 10 * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(final String key, final Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	@Override
	public Bitmap getBitmap(final String url) {
		return mCache.get(url);
	}

	@Override
	public void putBitmap(final String url, final Bitmap bitmap) {
		mCache.put(url, bitmap);
	}
}
