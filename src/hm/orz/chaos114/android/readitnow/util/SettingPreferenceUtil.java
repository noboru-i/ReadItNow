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
		final Map<String, String> map = (Map<String, String>) sp.getAll();
		if (map.size() == 0) {
			// 初期設定
			// TODO 初期値がlayout xmlとの２箇所に定義されるため微妙
			map.put("state", "");
			map.put("favorite", "");
			map.put("tag", "");
			map.put("contentType", "");
			map.put("sort", "newest");
			map.put("search", "");
		}
		return map;
	}

	private String getName(final int appWidgetId) {
		return "SETTING_" + appWidgetId;
	}

}
