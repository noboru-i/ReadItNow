package pocket4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pocket4j.action.Action;
import pocket4j.action.retrieve.RetrieveAction;
import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;
import pocket4j.util.HttpRequestUtil;
import android.util.Log;

public class Pocket implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String TAG = Pocket.class.getSimpleName();

	public static final String URL_V3_GET = "https://getpocket.com/v3/get";
	public static final String URL_V3_SEND = "https://getpocket.com/v3/send";

	/**
	 * TODO それぞれを別クラス（同一interfaceの実装）にするべき？
	 * 通常アクション列挙体
	 */
	public enum BasicAction {
		ADD("add"), // 追加
		ARCHIVE("archive"), // archiveに移動
		READD("readd"), // archiveから戻す
		FAVORITE("favorite"), // favoriteをマークする
		UNFAVORITE("unfavorite"), // favoriteから外す
		DELETE("delete"), // 削除する
		;

		private String actionName;

		private BasicAction(final String actionName) {
			this.actionName = actionName;
		}

		public String getActionName() {
			return actionName;
		}
	}

	private Authorization authorization;
	private final Configuration configuration;

	public Pocket(final Authorization authorization,
			final Configuration configuration) {
		this.authorization = authorization;
		this.configuration = configuration;
	}

	private String postRequest(final String url, final Action action)
			throws IOException {

		final String consumerKey = configuration.getApiKey();
		final String accessToken = authorization.getAccessToken();

		final JSONObject params = new JSONObject();
		try {
			params.put("consumer_key", consumerKey);
			params.put("access_token", accessToken);
			final Map<String, String> map = action.getRequestParams();
			if (map != null) {
				for (final String key : map.keySet()) {
					params.put(key, map.get(key));
				}
			}
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}

		return HttpRequestUtil.postJson(url, params);
	}

	public List<Item> retrieve(final RetrieveAction action)
			throws IOException {
		Log.d(TAG, "#retrieve");
		final String response = postRequest(URL_V3_GET, action);

		JSONObject object;
		try {
			object = new JSONObject(response);
		} catch (final JSONException e) {
			// 空のリストを返す
			return new ArrayList<Item>();
		}
		final JSONObject list = object.optJSONObject("list");
		if (list == null) {
			// listが取得できなかったため、空のリストを返す
			return new ArrayList<Item>();
		}
		final List<Item> items = new ArrayList<Item>();
		final Iterator<?> ite = list.keys();
		while (ite.hasNext()) {
			final Item item = new Item(list.optJSONObject((String) ite.next()));
			items.add(item);
		}

		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(final Item lhs, final Item rhs) {
				return lhs.getSortId() - rhs.getSortId();
			}
		});

		return items;
	}

	public void modify(final Action action)
			throws IOException {

		getRequest(URL_V3_SEND, action.getRequestParams());

		// TODO 後処理
	}

	private String getRequest(final String url, final Map<String, String> map)
			throws IOException {

		final String consumerKey = configuration.getApiKey();
		final String accessToken = authorization.getAccessToken();

		final Map<String, String> params = new HashMap<String, String>();
		params.put("consumer_key", consumerKey);
		params.put("access_token", accessToken);
		if (map != null) {
			for (final String key : map.keySet()) {
				params.put(key, map.get(key));
			}
		}

		return HttpRequestUtil.get(url, params);
	}

	public void setAuthorization(final Authorization authorization) {
		this.authorization = authorization;
	}
}
