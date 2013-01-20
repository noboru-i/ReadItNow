package hm.orz.chaos114.android.readitnow.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingPreferenceUtil {

	private final SharedPreferences sp;

	public SettingPreferenceUtil(final Context context, final int appWidgetId) {
		sp = context.getApplicationContext().getSharedPreferences(
				getName(appWidgetId), Context.MODE_PRIVATE);
	}

	public void putAll(final Map<String, String> source) {
		final Editor editor = sp.edit();
		for (final String key : source.keySet()) {
			editor.putString(key, source.get(key));
		}
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getAll() {
		return (Map<String, String>) sp.getAll();
	}

	private String getName(final int appWidgetId) {
		return "SETTING_" + appWidgetId;
	}

}
