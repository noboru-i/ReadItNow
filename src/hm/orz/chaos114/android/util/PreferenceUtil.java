package hm.orz.chaos114.android.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Preferenceのユーティリティクラス
 *
 * @author ishikuranoboru
 */
public class PreferenceUtil {

	private final SharedPreferences sp;

	public PreferenceUtil(final Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context
				.getApplicationContext());
	}

	public void putString(final String key, final String value) {
		final Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putInt(final String key, final int value) {
		final Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putBoolean(final String key, final boolean value) {
		final Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public String getString(final String key) {
		return sp.getString(key, null);
	}

	public int getInt(final String key) {
		return sp.getInt(key, 0);
	}

	public boolean getBoolean(final String key) {
		return sp.getBoolean(key, false);
	}

	public Map<String, ?> getAll() {
		return sp.getAll();
	}

	public void remove(final String key) {
		final Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
}
